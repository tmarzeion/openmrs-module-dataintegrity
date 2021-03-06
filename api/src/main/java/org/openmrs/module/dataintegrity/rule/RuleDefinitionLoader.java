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

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.dataintegrity.DataIntegrityException;
import org.openmrs.module.dataintegrity.DataIntegrityRule;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import groovy.lang.GroovyClassLoader;

@Component
public class RuleDefinitionLoader {
	
	public static final String GROOVY_DQ_RULES_PATH = "/dataQualityRules/";
	
	public Map<DataIntegrityRule, RuleDefinition> getRuleDefinitions(List<DataIntegrityRule> dataIntegrityRules) {
		Map<DataIntegrityRule, RuleDefinition> rulesWithDefn = new HashMap<>();
		
		for (DataIntegrityRule rule : dataIntegrityRules) {
			RuleDefinition ruleDefinition = getRuleInstance(rule);
			if (ruleDefinition != null) {
				rulesWithDefn.put(rule, ruleDefinition);
			}
		}
		return rulesWithDefn;
	}
	
	private RuleDefinition getRuleInstance(DataIntegrityRule rule) {
		
		if ("java".equals(rule.getHandlerConfig())) {
			return getJavaRuleInstance(rule.getHandlerClassname());
		} else if ("groovy".equals(rule.getHandlerConfig())) {
			return getGroovyRuleInstance(rule.getHandlerClassname());
		} else {
			throw new IllegalArgumentException("The rule type [" + rule.getHandlerConfig() + "] is not supported");
		}
	}
	
	private RuleDefinition getJavaRuleInstance(String ruleCode) {
		RuleDefinition ruleDefinition = null;
		try {
			Class<?> ruleClass = Context.loadClass(ruleCode);
			Object o = ruleClass.newInstance();
			ruleDefinition = (RuleDefinition) o;
		}
		catch (Exception e) {
			throw new DataIntegrityException("Failed to load java rule" + getStackTrace(e));
		}
		return ruleDefinition;
	}
	
	private RuleDefinition getGroovyRuleInstance(String groovyFileName) {
		RuleDefinition ruleDefinition = null;
		
		try {
			Class clazz = new GroovyClassLoader().parseClass(new File(getGroovyClassPath(groovyFileName)));
			ruleDefinition = (RuleDefinition) clazz.newInstance();
		}
		catch (Exception e) {
			throw new DataIntegrityException("Failed to load groovy rule " + getStackTrace(e));
		}
		return ruleDefinition;
	}
	
	private String getGroovyClassPath(String groovyFileName) {
		return OpenmrsUtil.getApplicationDataDirectory() + GROOVY_DQ_RULES_PATH + groovyFileName;
	}
}
