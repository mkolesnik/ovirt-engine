/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.v3.adapters;

import static org.ovirt.engine.api.v3.adapters.V3InAdapters.adaptIn;

import org.ovirt.engine.api.model.Disks;
import org.ovirt.engine.api.model.Quota;
import org.ovirt.engine.api.model.Users;
import org.ovirt.engine.api.model.Vms;
import org.ovirt.engine.api.v3.V3Adapter;
import org.ovirt.engine.api.v3.types.V3Quota;

public class V3QuotaInAdapter implements V3Adapter<V3Quota, Quota> {
    @Override
    public Quota adapt(V3Quota from) {
        Quota to = new Quota();
        if (from.isSetLinks()) {
            to.getLinks().addAll(adaptIn(from.getLinks()));
        }
        if (from.isSetActions()) {
            to.setActions(adaptIn(from.getActions()));
        }
        if (from.isSetClusterHardLimitPct()) {
            to.setClusterHardLimitPct(from.getClusterHardLimitPct());
        }
        if (from.isSetClusterSoftLimitPct()) {
            to.setClusterSoftLimitPct(from.getClusterSoftLimitPct());
        }
        if (from.isSetComment()) {
            to.setComment(from.getComment());
        }
        if (from.isSetDataCenter()) {
            to.setDataCenter(adaptIn(from.getDataCenter()));
        }
        if (from.isSetDescription()) {
            to.setDescription(from.getDescription());
        }
        if (from.isSetDisks()) {
            to.setDisks(new Disks());
            to.getDisks().getDisks().addAll(adaptIn(from.getDisks().getDisks()));
        }
        if (from.isSetId()) {
            to.setId(from.getId());
        }
        if (from.isSetHref()) {
            to.setHref(from.getHref());
        }
        if (from.isSetName()) {
            to.setName(from.getName());
        }
        if (from.isSetStorageHardLimitPct()) {
            to.setStorageHardLimitPct(from.getStorageHardLimitPct());
        }
        if (from.isSetStorageSoftLimitPct()) {
            to.setStorageSoftLimitPct(from.getStorageSoftLimitPct());
        }
        if (from.isSetUsers()) {
            to.setUsers(new Users());
            to.getUsers().getUsers().addAll(adaptIn(from.getUsers().getUsers()));
        }
        if (from.isSetVms()) {
            to.setVms(new Vms());
            to.getVms().getVms().addAll(adaptIn(from.getVms().getVMs()));
        }
        return to;
    }
}
