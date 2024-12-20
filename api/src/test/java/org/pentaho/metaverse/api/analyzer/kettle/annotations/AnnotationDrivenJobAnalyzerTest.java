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


package org.pentaho.metaverse.api.analyzer.kettle.annotations;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.di.core.Result;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.dictionary.MetaverseTransientNode;
import org.pentaho.metaverse.api.MetaverseAnalyzerException;
import org.pentaho.metaverse.api.MetaverseComponentDescriptor;
import org.pentaho.metaverse.api.Namespace;
import org.pentaho.metaverse.api.model.BaseMetaverseBuilder;

import static org.mockito.Mockito.any;
import static org.pentaho.dictionary.DictionaryConst.NODE_TYPE_FILE;


@RunWith ( MockitoJUnitRunner.StrictStubs.class )
public class AnnotationDrivenJobAnalyzerTest {

  @Mock AnnotationDrivenStepMetaAnalyzer stepAnalyzer;

  @Test
  public void jobCanReferenceStepMetaForAnalysis() throws MetaverseAnalyzerException {
    TestJobEntry jobEntry = new TestJobEntry();
    AnnotationDrivenJobAnalyzer jobAnalyzer = new AnnotationDrivenJobAnalyzer( jobEntry ) {
      @Override AnnotationDrivenStepMetaAnalyzer createStepAnalyzer( BaseStepMeta baseStepMeta ) {
        return stepAnalyzer;
      }
    };
    jobAnalyzer.setMetaverseBuilder( new BaseMetaverseBuilder( new TinkerGraph() ) );
    jobAnalyzer.analyze( new MetaverseComponentDescriptor( "root", "job", new Namespace( "names" ) ), jobEntry );
    Mockito.verify( stepAnalyzer ).customAnalyze( any( TestStepMeta.class ), any( MetaverseTransientNode.class ) );
  }

  @SuppressWarnings ( "unused" )
  static class TestJobEntry extends JobEntryBase implements JobEntryInterface {
    @Override public Result execute( Result prev_result, int nr ) {
      return new Result();
    }

    @Override public JobMeta getParentJobMeta() {
      return new JobMeta();
    }

    @Override public Job getParentJob() {
      return new Job( "test", "test.kjb", new String[]{} );
    }

    @Metaverse.InternalStepMeta
    public BaseStepMeta testStepMeta() {
      return new TestStepMeta();
    }
  }

  static class TestStepMeta extends BaseStepMeta {
    static final String TEST_NODE = "TestNode";

    @Metaverse.Node( name = TEST_NODE, type = NODE_TYPE_FILE )
    final String node = "MyNode";
    @Metaverse.Property( name = "prop1", parentNodeName = TEST_NODE )
    final String prop1 = "Property One Value";

    @Metaverse.Property( name = "prop2", parentNodeName = TEST_NODE )
    final String prop2 = "Property Two Value";
  }
}
