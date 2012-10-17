package org.ovirt.engine.core.bll.quota;

import org.ovirt.engine.core.common.businessentities.storage_pool;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.auditloghandling.AuditLogableBase;

import java.util.ArrayList;
import java.util.List;

public class QuotaConsumptionParametersWrapper {

    private List<QuotaConsumptionParameter> parameters;

    private AuditLogableBase auditLogable;
    private List<String> canDoActionMessages;

    public QuotaConsumptionParametersWrapper(AuditLogableBase auditLogable, List<String> canDoActionMessages) {
        this.auditLogable = auditLogable;
        this.canDoActionMessages = canDoActionMessages;
    }

    public List<String> getCanDoActionMessages() {
        return canDoActionMessages;
    }

    public void setCanDoActionMessages(List<String> canDoActionMessages) {
        this.canDoActionMessages = canDoActionMessages;
    }

    public storage_pool getStoragePool() {
        return auditLogable.getStoragePool();
    }

    public Guid getStoragePoolId() {
        return getStoragePool().getId();
    }

    public List<QuotaConsumptionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<QuotaConsumptionParameter> parameters) {
        this.parameters = parameters;
    }

    public AuditLogableBase getAuditLogable() {
        return this.auditLogable;
    }

    public void setAuditLogable(AuditLogableBase auditLogable) {
        this.auditLogable = auditLogable;
    }

    @Override
    public QuotaConsumptionParametersWrapper clone() throws CloneNotSupportedException {
        QuotaConsumptionParametersWrapper cloneWrapper = new QuotaConsumptionParametersWrapper(getAuditLogable(),
                canDoActionMessages);
        cloneWrapper.setParameters(new ArrayList<QuotaConsumptionParameter>());

        if (getParameters() != null) {
            for (QuotaConsumptionParameter parameter : getParameters()) {
                cloneWrapper.getParameters().add(parameter.clone());
            }
        }

        return cloneWrapper;
    }
}
