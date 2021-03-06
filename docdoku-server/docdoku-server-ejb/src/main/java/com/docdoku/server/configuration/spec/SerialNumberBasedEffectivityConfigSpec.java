/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.docdoku.server.configuration.spec;

import com.docdoku.core.product.PartIteration;
import com.docdoku.core.product.PartLink;
import com.docdoku.core.product.PartMaster;

import java.util.List;

/**
 * A kind of {@link EffectivityConfigSpec} based on serial number.
 * 
 * @author Florent Garin
 * @version 1.1, 30/10/11
 * @since   V1.1
 */
public class SerialNumberBasedEffectivityConfigSpec extends EffectivityConfigSpec {

    /**
     * The serial number of the particular item specified by the context.
     */
    private String number;
    public SerialNumberBasedEffectivityConfigSpec() {
    }

    @Override
    public PartIteration filterPartIteration(PartMaster partMaster) {
        // TODO : implement filter
        return null;
    }

    @Override
    public PartLink filterPartLink(List<PartLink> path) {
        // TODO : implement filter
        return null;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
    
}
