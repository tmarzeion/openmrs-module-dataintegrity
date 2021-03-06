/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 *
 */

package org.openmrs.module.dataintegrity.rule;

import java.util.List;

import org.openmrs.module.dataintegrity.DataIntegrityRule;

public interface RuleDefinition<T> {
	
	/**
	 * Evaluate the defined rule returning any entities that violate it
	 *
	 * @return
	 */
	public List<RuleResult<T>> evaluate();
	
	/**
	 * Return the DataIntegrityRule instance that is used to execute this rule. A null value for this method will not save
	 * any metadata for the rule
	 *
	 * @return
	 */
	public DataIntegrityRule getRule();
}
