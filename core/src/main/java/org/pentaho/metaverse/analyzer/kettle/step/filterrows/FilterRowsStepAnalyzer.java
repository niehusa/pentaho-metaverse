/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.metaverse.analyzer.kettle.step.filterrows;

import org.pentaho.di.core.Condition;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.steps.filterrows.FilterRowsMeta;
import org.pentaho.dictionary.DictionaryConst;
import org.pentaho.metaverse.api.ChangeType;
import org.pentaho.metaverse.api.IMetaverseNode;
import org.pentaho.metaverse.api.StepField;
import org.pentaho.metaverse.api.analyzer.kettle.ComponentDerivationRecord;
import org.pentaho.metaverse.api.analyzer.kettle.step.StepAnalyzer;
import org.pentaho.metaverse.api.analyzer.kettle.step.IClonableStepAnalyzer;
import org.pentaho.metaverse.api.model.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class FilterRowsStepAnalyzer extends StepAnalyzer<FilterRowsMeta> {

  private static final Logger LOGGER = LoggerFactory.getLogger( FilterRowsStepAnalyzer.class );
  public static final String DATA_FLOW_CONDITION = "dataFlowCondition";

  @Override
  protected void customAnalyze( FilterRowsMeta stepMeta, IMetaverseNode stepNode ) {
    // add the filter condition as properties on the step node

    final Condition condition = stepMeta.getCondition();

    if ( condition != null ) {
      String filterCondition = condition.toString();
      Operation operation = new Operation( "filter", ChangeType.DATA_FLOW, DATA_FLOW_CONDITION, filterCondition );

      ComponentDerivationRecord changeRecord = new ComponentDerivationRecord( stepNode.getName(), ChangeType.DATA_FLOW );
      changeRecord.addOperation( operation );
      stepNode.setProperty( DictionaryConst.PROPERTY_OPERATIONS, changeRecord.toString() );
    }
  }

  @Override
  protected Set<StepField> getUsedFields( FilterRowsMeta meta ) {
    // add uses links to all of the fields that are part of the filter condition
    Set<StepField> usedFields = new HashSet<>();

    final Condition condition = meta.getCondition();

    if ( condition != null ) {
      for ( String usedField : condition.getUsedFields() ) {
        usedFields.addAll( createStepFields( usedField, getInputs() ) );
      }
    }
    return usedFields;
  }

  @Override
  public Set<Class<? extends BaseStepMeta>> getSupportedSteps() {
    Set<Class<? extends BaseStepMeta>> supportedSteps = new HashSet<>();
    supportedSteps.add( FilterRowsMeta.class );
    return supportedSteps;
  }

  @Override
  public IClonableStepAnalyzer newInstance() {
    return new FilterRowsStepAnalyzer();
  }
}
