/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.v3.adapters;

import static org.ovirt.engine.api.v3.adapters.V3InAdapters.adaptIn;

import org.ovirt.engine.api.model.CustomProperties;
import org.ovirt.engine.api.model.VnicProfile;
import org.ovirt.engine.api.v3.V3Adapter;
import org.ovirt.engine.api.v3.types.V3VnicProfile;

public class V3VnicProfileInAdapter implements V3Adapter<V3VnicProfile, VnicProfile> {
    @Override
    public VnicProfile adapt(V3VnicProfile from) {
        VnicProfile to = new VnicProfile();
        if (from.isSetLinks()) {
            to.getLinks().addAll(adaptIn(from.getLinks()));
        }
        if (from.isSetActions()) {
            to.setActions(adaptIn(from.getActions()));
        }
        if (from.isSetComment()) {
            to.setComment(from.getComment());
        }
        if (from.isSetCustomProperties()) {
            to.setCustomProperties(new CustomProperties());
            to.getCustomProperties().getCustomProperties().addAll(adaptIn(from.getCustomProperties().getCustomProperty()));
        }
        if (from.isSetDescription()) {
            to.setDescription(from.getDescription());
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
        if (from.isSetNetwork()) {
            to.setNetwork(adaptIn(from.getNetwork()));
        }
        if (from.isSetPassThrough()) {
            to.setPassThrough(adaptIn(from.getPassThrough()));
        }
        if (from.isSetPortMirroring()) {
            to.setPortMirroring(from.isPortMirroring());
        }
        if (from.isSetQos()) {
            to.setQos(adaptIn(from.getQos()));
        }
        return to;
    }
}
