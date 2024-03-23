package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.InventoryItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final InventoryItemService inventoryItemService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderService orderService, InventoryItemService inventoryItemService, Converter converter) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.inventoryItemService = inventoryItemService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAllPayments() {

        try {
            return paymentRepository
                    .findAll()
                    .stream()
                    .map(paymentEntity -> converter.toResponse(paymentEntity, PaymentResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar pagamentos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar pagamentos: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        paymentRequest.setId(null);

        PaymentEntity paymentEntity = buildPaymentFromRequest(paymentRequest);

        try {
            paymentRepository.save(paymentEntity);

            logger.info("Pagamento criado: {}", paymentEntity);
            return converter.toResponse(paymentEntity, PaymentResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o pagamento: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse findPaymentById(Long id) {

        return paymentRepository
                .findById(id)
                .map(paymentEntity -> converter.toResponse(paymentEntity, PaymentResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Pagamento não encontrado com o ID: {}", id);
                    return new NotFoundException("Pagamento não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public void deletePayment(Long id) {

        PaymentEntity paymentEntity = findPaymentEntityById(id);

        try {
            paymentRepository.deleteById(id);
            logger.warn("Pagamento removido: {}", paymentEntity);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o pagamento: " + exception);
        }
    }

    public PaymentEntity findPaymentEntityById(Long id) {

        return paymentRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pagamento não encontrado com o ID: {}", id);
                    return new NotFoundException("Pagamento não encontrado com o ID: " + id + ".");
                });
    }

    public PaymentEntity buildPaymentFromRequest(PaymentRequest paymentRequest) {

        OrderEntity orderToBePaid = orderService.findOrderEntityById(paymentRequest.getOrderId());

        validatePaymentConditions(orderToBePaid);

        PaymentEntity paymentEntity = new PaymentEntity(Instant.now(), orderToBePaid);

        orderToBePaid.setPaymentEntity(paymentEntity);
        orderToBePaid.setOrderStatus(OrderStatus.PAID);
        orderService.savePaidOrder(orderToBePaid);

        return paymentEntity;
    }

    public void validatePaymentConditions(OrderEntity order) {

        if (orderService.isOrderPaid(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: pedido já pago.");

        } else if (!orderService.isOrderItemsPresent(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: não há item no pedido.");

        } else if (!orderService.isAddressClientPresent(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: endereço de entrega não cadastrado.");
        }
    }
}

