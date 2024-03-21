package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import com.rogeriogregorio.ecommercemanager.services.UserService;
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
    private final UserService userService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderService orderService, UserService userService, Converter converter) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.userService = userService;
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

        if (orderService.isOrderPaid(orderToBePaid)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: pedido já pago.");
        }

        if (!orderService.isOrderItemsNotEmpty(orderToBePaid)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: não há item no pedido.");
        }

        if (!userService.isAddressPresent(orderToBePaid.getClient())) {
            throw new IllegalStateException("Não foi possível processar o pagamento: endereço de entrega não cadastrado.");
        }

        PaymentEntity paymentEntity = new PaymentEntity(Instant.now(), orderToBePaid);

        orderToBePaid.setPaymentEntity(paymentEntity);
        orderToBePaid.setOrderStatus(OrderStatus.PAID);
        orderService.savePaidOrder(orderToBePaid);

        return paymentEntity;
    }
}

