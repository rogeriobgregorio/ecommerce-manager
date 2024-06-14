package com.rogeriogregorio.ecommercemanager.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class EmailDetailsDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sender;
    private String recipient;
    private String subject;
    private String templateName;
    private Map<String, String> replacements;

    public EmailDetailsDto() {
    }

    private EmailDetailsDto(Builder builder) {
        setSender(builder.sender);
        setRecipient(builder.recipient);
        setSubject(builder.subject);
        setTemplateName(builder.templateName);
        setReplacements(builder.replacements);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }

    public void setReplacements(Map<String, String> replacements) {
        this.replacements = replacements;
    }

    public static final class Builder {
        private String sender;
        private String recipient;
        private String subject;
        private String templateName;
        private Map<String, String> replacements;

        private Builder() {
        }

        public Builder withSender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder withRecipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder withTemplateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public Builder withReplacements(Map<String, String> replacements) {
            this.replacements = replacements;
            return this;
        }

        public EmailDetailsDto build() {
            return new EmailDetailsDto(this);
        }
    }
}
