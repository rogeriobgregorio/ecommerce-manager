package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final Converter<PaymentRequest, PaymentEntity, PaymentResponse> paymentConverter;
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, Converter<PaymentRequest, PaymentEntity, PaymentResponse> paymentConverter) {
        this.paymentRepository = paymentRepository;
        this.paymentConverter = paymentConverter;
    }

    public List<PaymentResponse> findAllPayments() {

        try {
            return paymentRepository
                    .findAll()
                    .stream()
                    .map(paymentConverter::entityToResponse)
                    .collect(Collectors.toList());

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar pagamentos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar pagamentos.", exception);
        }
    }

    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        paymentRequest.setId(null);

        PaymentEntity paymentEntity = paymentConverter.requestToEntity(paymentRequest);

        try {
            paymentRepository.save(paymentEntity);
            logger.info("Pagamento criado: {}", paymentEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar criar o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o pagamento.", exception);
        }

        return paymentConverter.entityToResponse(paymentEntity);
    }

    public PaymentResponse findPaymentById(Long id) {

        return paymentRepository
                .findById(id)
                .map(paymentConverter::entityToResponse)
                .orElseThrow(() -> {
                    logger.warn("Pagamento não encontrado com o ID: {}", id);
                    return new NotFoundException("Pagamento não encontrado com o ID: " + id + ".");
                });
    }

    public PaymentResponse updatePayment(PaymentRequest paymentRequest) {

        PaymentEntity paymentEntity = paymentConverter.requestToEntity(paymentRequest);

        paymentRepository.findById(paymentEntity.getId()).orElseThrow(() -> {
            logger.warn("Pagamento não encontrado com o ID: {}", paymentEntity.getId());
            return new NotFoundException("Pagamento não encontrado com o ID: " + paymentEntity.getId() + ".");
        });

        try {
            paymentRepository.save(paymentEntity);
            logger.info("Pagamento atualizado: {}", paymentEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o pagamento.", exception);
        }

        return paymentConverter.entityToResponse(paymentEntity);
    }

    public void deletePayment(Long id) {

        paymentRepository.findById(id).orElseThrow(() -> {
            logger.warn("Pagamento não encontrado com o ID: {}", id);
            return new NotFoundException("Pagamento não encontrado com o ID: " + id + ".");
        });

        try {
            paymentRepository.deleteById(id);
            logger.warn("Pagamento removido: {}", id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o pagamento.", exception);
        }

    }
}
