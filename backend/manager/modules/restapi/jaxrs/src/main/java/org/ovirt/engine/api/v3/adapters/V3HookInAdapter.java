/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.v3.adapters;

import static org.ovirt.engine.api.v3.adapters.V3InAdapters.adaptIn;

import org.ovirt.engine.api.model.Hook;
import org.ovirt.engine.api.v3.V3Adapter;
import org.ovirt.engine.api.v3.types.V3Hook;

public class V3HookInAdapter implements V3Adapter<V3Hook, Hook> {
    @Override
    public Hook adapt(V3Hook from) {
        Hook to = new Hook();
        if (from.isSetLinks()) {
            to.getLinks().addAll(adaptIn(from.getLinks()));
        }
        if (from.isSetActions()) {
            to.setActions(adaptIn(from.getActions()));
        }
        if (from.isSetComment()) {
            to.setComment(from.getComment());
        }
        if (from.isSetDescription()) {
            to.setDescription(from.getDescription());
        }
        if (from.isSetEventName()) {
            to.setEventName(from.getEventName());
        }
        if (from.isSetHost()) {
            to.setHost(adaptIn(from.getHost()));
        }
        if (from.isSetId()) {
            to.setId(from.getId());
        }
        if (from.isSetHref()) {
            to.setHref(from.getHref());
        }
        if (from.isSetMd5()) {
            to.setMd5(from.getMd5());
        }
        if (from.isSetName()) {
            to.setName(from.getName());
        }
        return to;
    }
}
