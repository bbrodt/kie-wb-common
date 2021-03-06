/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.backend.ApplicationFactoryManager;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.BPMNGraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.AssignmentsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.BooleanTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertySerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ColorTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.DoubleTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.EnumTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.IntegerTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.StringTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.VariablesTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.AssigneeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimePropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimePropertySetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.binding.RuntimeBindableMorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

// TODO: Use Archillian to avoid all that CDI mockings.
@RunWith(MockitoJUnitRunner.class)
public class BPMNDiagramMarshallerTest {

    private static final String BPMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);

    private static final String BPMN_BASIC = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/basic.bpmn";
    private static final String BPMN_EVALUATION = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/evaluation.bpmn";
    private static final String BPMN_LANES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/lanes.bpmn";
    private static final String BPMN_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryIntmEvent.bpmn";
    private static final String BPMN_NOT_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/notBoundaryIntmEvent.bpmn";
    private static final String BPMN_PROCESSVARIABLES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processVariables.bpmn";
    private static final String BPMN_USERTASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignments.bpmn";
    private static final String BPMN_BUSINESSRULETASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTaskAssignments.bpmn";
    private static final String BPMN_STARTEVENTASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startEventAssignments.bpmn";
    private static final String BPMN_ENDEVENTASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endEventAssignments.bpmn";
    private static final String BPMN_PROCESSPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processProperties.bpmn";
    private static final String BPMN_BUSINESSRULETASKRULEFLOWGROUP = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTask.bpmn";
    private static final String BPMN_REUSABLE_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/reusableSubprocessCalledElement.bpmn";
    private static final String BPMN_SCRIPTTASK = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/scriptTask.bpmn";
    private static final String BPMN_USERTASKASSIGNEES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignees.bpmn";
    private static final String BPMN_USERTASKPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskProperties.bpmn";
    private static final String BPMN_SEQUENCEFLOW = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/sequenceFlow.bpmn";
    private static final String BPMN_XORGATEWAY = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/xorGateway.bpmn";
    private static final String BPMN_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/timerEvent.bpmn";
    private static final String BPMN_SIMULATIONPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/simulationProperties.bpmn";

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    AdapterRegistry adapterRegistry;

    @Mock
    BeanManager beanManager;

    @Mock
    ApplicationFactoryManager applicationFactoryManager;

    EdgeFactory<Object> connectionEdgeFactory;
    NodeFactory<Object> viewNodeFactory;
    DefinitionUtils definitionUtils;
    GraphUtils graphUtils;

    GraphCommandManager commandManager;
    GraphCommandFactory commandFactory;

    GraphFactory bpmnGraphFactory;

    Bpmn2OryxIdMappings oryxIdMappings;
    Bpmn2OryxPropertyManager oryxPropertyManager;
    Bpmn2OryxManager oryxManager;

    TestScopeModelFactory testScopeModelFactory;
    BPMNGraphObjectBuilderFactory objectBuilderFactory;

    TaskTypeMorphDefinition taskMorphDefinition;

    private BPMNDiagramMarshaller tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        definitionUtils = new DefinitionUtils(definitionManager,
                applicationFactoryManager);
        graphUtils = new GraphUtils(definitionManager);
        testScopeModelFactory = new TestScopeModelFactory(new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build());
        // Definition manager.
        final RuntimeDefinitionAdapter definitionAdapter = new RuntimeDefinitionAdapter(definitionUtils);
        final RuntimeDefinitionSetAdapter definitionSetAdapter = new RuntimeDefinitionSetAdapter(definitionAdapter);
        final RuntimePropertySetAdapter propertySetAdapter = new RuntimePropertySetAdapter();
        final RuntimePropertyAdapter propertyAdapter = new RuntimePropertyAdapter();
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        commandManager = new GraphCommandManagerImpl(null,
                null,
                null);
        commandFactory = new GraphCommandFactory();
        connectionEdgeFactory = new EdgeFactoryImpl(definitionManager);
        viewNodeFactory = new NodeFactoryImpl(definitionUtils);
        bpmnGraphFactory = new GraphFactoryImpl(definitionManager);
        doAnswer(invocationOnMock -> {
            String id = (String) invocationOnMock.getArguments()[0];
            return testScopeModelFactory.build(id);
        }).when(applicationFactoryManager).newDefinition(anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String id = (String) invocationOnMock.getArguments()[1];
            if (BPMNDefinitionSet.class.getName().equals(id)) {
                Graph graph = (Graph) bpmnGraphFactory.build(uuid,
                        BPMN_DEF_SET_ID);
                return graph;
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = RuntimeDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    Node node = viewNodeFactory.build(uuid,
                            model);
                    return node;
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    Edge edge = connectionEdgeFactory.build(uuid,
                            model);
                    return edge;
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            Class type = (Class) invocationOnMock.getArguments()[1];
            String id = BindableAdapterUtils.getGenericClassName(type);
            if (BPMNDefinitionSet.class.equals(type)) {
                Graph graph = (Graph) bpmnGraphFactory.build(uuid,
                        BPMN_DEF_SET_ID);
                return graph;
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = RuntimeDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    Node node = viewNodeFactory.build(uuid,
                            model);
                    return node;
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    Edge edge = connectionEdgeFactory.build(uuid,
                            model);
                    return edge;
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                any(Class.class));
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String defSetId = (String) invocationOnMock.getArguments()[1];
            final Graph graph = (Graph) applicationFactoryManager.newElement(uuid,
                    defSetId);
            final DiagramImpl result = new DiagramImpl(uuid,
                    new MetadataImpl.MetadataImplBuilder(defSetId).build());
            result.setGraph(graph);
            return result;
        }).when(applicationFactoryManager).newDiagram(anyString(),
                anyString(),
                any(Metadata.class));
        // Bpmn 2 oryx stuff.
        oryxIdMappings = new Bpmn2OryxIdMappings(definitionManager);
        StringTypeSerializer stringTypeSerializer = new StringTypeSerializer();
        BooleanTypeSerializer booleanTypeSerializer = new BooleanTypeSerializer();
        ColorTypeSerializer colorTypeSerializer = new ColorTypeSerializer();
        DoubleTypeSerializer doubleTypeSerializer = new DoubleTypeSerializer();
        IntegerTypeSerializer integerTypeSerializer = new IntegerTypeSerializer();
        EnumTypeSerializer enumTypeSerializer = new EnumTypeSerializer(definitionUtils);
        AssignmentsTypeSerializer assignmentsTypeSerializer = new AssignmentsTypeSerializer();
        VariablesTypeSerializer variablesTypeSerializer = new VariablesTypeSerializer();
        List<Bpmn2OryxPropertySerializer<?>> propertySerializers = new LinkedList<>();
        propertySerializers.add(stringTypeSerializer);
        propertySerializers.add(booleanTypeSerializer);
        propertySerializers.add(colorTypeSerializer);
        propertySerializers.add(doubleTypeSerializer);
        propertySerializers.add(integerTypeSerializer);
        propertySerializers.add(enumTypeSerializer);
        propertySerializers.add(assignmentsTypeSerializer);
        propertySerializers.add(variablesTypeSerializer);
        oryxPropertyManager = new Bpmn2OryxPropertyManager(propertySerializers);
        oryxManager = new Bpmn2OryxManager(oryxIdMappings,
                oryxPropertyManager);
        oryxManager.init();
        // Marshalling factories.
        objectBuilderFactory = new BPMNGraphObjectBuilderFactory(definitionManager,
                oryxManager);
        taskMorphDefinition = new TaskTypeMorphDefinition();
        Collection<MorphDefinition> morphDefinitions = new ArrayList<MorphDefinition>() {{
            add(taskMorphDefinition);
        }};
        RuntimeBindableMorphAdapter<Object> morphAdapter =
                new RuntimeBindableMorphAdapter(definitionUtils,
                        applicationFactoryManager,
                        morphDefinitions);
        when(adapterRegistry.getMorphAdapter(eq(UserTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(NoneTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(ScriptTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(BusinessRuleTask.class))).thenReturn(morphAdapter);
        GraphIndexBuilder<?> indexBuilder = new MapIndexBuilder();
        // The tested BPMN marshaller.
        tested = new BPMNDiagramMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                objectBuilderFactory,
                definitionManager,
                graphUtils,
                indexBuilder,
                oryxManager,
                applicationFactoryManager,
                commandManager,
                commandFactory);
    }

    // 4 nodes expected: BPMNDiagram, StartNode, Task and EndNode
    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBasic() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BASIC);
        assertDiagram(diagram,
                4);
        assertEquals("Basic process",
                diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> task1 = diagram.getGraph().getNode("810797AB-7D09-4E1F-8A5B-96C424E4B031");
        assertTrue(task1.getContent().getDefinition() instanceof NoneTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEvaluation() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVALUATION);
        assertDiagram(diagram,
                8);
        assertEquals("Evaluation",
                diagram.getMetadata().getTitle());
        Node<? extends View, ?> task1 = diagram.getGraph().getNode("_88233779-B395-4B8C-A086-9EF43698426C");
        Node<? extends View, ?> task2 = diagram.getGraph().getNode("_AE5BF0DC-B720-4FDE-9499-5ED89D41FB1A");
        Node<? extends View, ?> task3 = diagram.getGraph().getNode("_6063D302-9D81-4C86-920B-E808A45377C2");
        assertTrue(task1.getContent().getDefinition() instanceof UserTask);
        assertTrue(task2.getContent().getDefinition() instanceof UserTask);
        assertTrue(task3.getContent().getDefinition() instanceof UserTask);
        // Assert bounds.
        Bounds task1Bounds = task1.getContent().getBounds();
        Bounds.Bound task1ULBound = task1Bounds.getUpperLeft();
        Bounds.Bound task1LRBound = task1Bounds.getLowerRight();
        assertEquals(648d,
                task1ULBound.getX(),
                0);
        assertEquals(149d,
                task1ULBound.getY(),
                0);
        assertEquals(784d,
                task1LRBound.getX(),
                0);
        assertEquals(197d,
                task1LRBound.getY(),
                0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallProcessVariables() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSVARIABLES);
        assertDiagram(diagram,
                8);
        assertEquals("ProcessVariables",
                diagram.getMetadata().getTitle());
        ProcessVariables variables = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BPMNDiagram) {
                    BPMNDiagram bpmnDiagram = (BPMNDiagram) oDefinition;
                    variables = bpmnDiagram.getProcessData().getProcessVariables();
                    break;
                }
            }
        }
        assertEquals(variables.getValue(),
                "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String");
        Node<? extends Definition, ?> diagramNode = diagram.getGraph().getNode("_luRBMdEjEeWXpsZ1tNStKQ");
        assertTrue(diagramNode.getContent().getDefinition() instanceof BPMNDiagram);
        BPMNDiagram bpmnDiagram = (BPMNDiagram) diagramNode.getContent().getDefinition();
        assertTrue(bpmnDiagram.getProcessData() != null);
        assertTrue(bpmnDiagram.getProcessData().getProcessVariables() != null);
        variables = bpmnDiagram.getProcessData().getProcessVariables();
        assertEquals(variables.getValue(),
                "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallProcessProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSPROPERTIES);
        assertDiagram(diagram,
                4);
        assertEquals("BPSimple",
                diagram.getMetadata().getTitle());
        DiagramSet diagramProperties = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BPMNDiagram) {
                    BPMNDiagram bpmnDiagram = (BPMNDiagram) oDefinition;
                    diagramProperties = bpmnDiagram.getDiagramSet();
                    break;
                }
            }
        }
        assertEquals("BPSimple",
                diagramProperties.getName().getValue());
        assertEquals("\n" +
                        "        This is a simple process\n" +
                        "    ",
                diagramProperties.getDocumentation().getValue());
        assertEquals("JDLProj.BPSimple",
                diagramProperties.getId().getValue());
        assertEquals("org.jbpm",
                diagramProperties.getPackageProperty().getValue());
        assertEquals(Boolean.valueOf(true),
                diagramProperties.getExecutable().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNMENTS);
        assertDiagram(diagram,
                8);
        assertEquals("UserTaskAssignments",
                diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> selfEvaluationNode = diagram.getGraph().getNode("_6063D302-9D81-4C86-920B-E808A45377C2");
        UserTask selfEvaluationTask = (UserTask) selfEvaluationNode.getContent().getDefinition();
        assertEquals(selfEvaluationTask.getTaskType().getValue(),
                TaskTypes.USER);
        DataIOSet dataIOSet = selfEvaluationTask.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals(assignmentsinfo.getValue(),
                "|reason:com.test.Reason,Comment:Object,Skippable:Object||performance:Object|[din]reason->reason,[dout]performance->performance");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBusinessRuleTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKASSIGNMENTS);
        assertDiagram(diagram,
                4);
        assertEquals("BusinessRuleTaskAssignments",
                diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> businessRuleNode = diagram.getGraph().getNode("_45C2C340-D1D0-4D63-8419-EF38F9E73507");
        BusinessRuleTask businessRuleTask = (BusinessRuleTask) businessRuleNode.getContent().getDefinition();
        assertEquals(businessRuleTask.getTaskType().getValue(),
                TaskTypes.BUSINESS_RULE);
        DataIOSet dataIOSet = businessRuleTask.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals(assignmentsinfo.getValue(),
                "|input1:String,input2:String||output1:String,output2:String|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv2,[dout]output2->pv2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartEventAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTEVENTASSIGNMENTS);
        assertDiagram(diagram,
                8);
        assertEquals("StartEventAssignments",
                diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startEventNode = diagram.getGraph().getNode("_106AADA7-E381-4736-8BD6-7EB47A1D096B");
        StartNoneEvent startNoneEvent = (StartNoneEvent) startEventNode.getContent().getDefinition();
        DataIOSet dataIOSet = startNoneEvent.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||StartEventOutput1:String||[dout]StartEventOutput1->employee",
                assignmentsinfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndEventAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDEVENTASSIGNMENTS);
        assertDiagram(diagram,
                8);
        assertEquals("EndEventAssignments",
                diagram.getMetadata().getTitle());

        Node<? extends Definition, ?> endTerminateEventNode = diagram.getGraph().getNode("_0493ECBF-3C0F-4086-9979-2391CE740AA8");
        EndTerminateEvent endTerminateEvent = (EndTerminateEvent) endTerminateEventNode.getContent().getDefinition();
        DataIOSet dataIOSet = endTerminateEvent.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals("EndTermEventInput1:String||||[din]employee->EndTermEventInput1",
                assignmentsinfo.getValue());

        Node<? extends Definition, ?> endNoneEventNode = diagram.getGraph().getNode("_4824B9FD-6097-4E06-9E7E-2CE5E7601BC5");
        EndNoneEvent endNoneEvent = (EndNoneEvent) endNoneEventNode.getContent().getDefinition();
        DataIOSet dataIOSet2 = endNoneEvent.getDataIOSet();
        AssignmentsInfo assignmentsinfo2 = dataIOSet2.getAssignmentsinfo();
        assertEquals("EndNoneEventInput1:String||||[din]reason->EndNoneEventInput1",
                assignmentsinfo2.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskAssignees() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNEES);
        assertDiagram(diagram,
                6);
        assertEquals("UserGroups",
                diagram.getMetadata().getTitle());
        AssigneeSet assigneeSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    assigneeSet = userTask.getAssigneeSet();
                    break;
                }
            }
        }
        assertEquals("user,user1",
                assigneeSet.getActors().getValue());
        assertEquals("admin,kiemgmt",
                assigneeSet.getGroupid().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKPROPERTIES);
        assertDiagram(diagram,
                4);
        assertEquals("MyBP",
                diagram.getMetadata().getTitle());
        UserTaskExecutionSet userTaskExecutionSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    userTaskExecutionSet = userTask.getExecutionSet();
                    break;
                }
            }
        }
        assertEquals("MyUserTask",
                userTaskExecutionSet.getTaskName().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallSimulationProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SIMULATIONPROPERTIES);
        assertDiagram(diagram,
                4);
        assertEquals("SimulationProperties",
                diagram.getMetadata().getTitle());

        SimulationSet simulationSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    simulationSet = userTask.getSimulationSet();
                    break;
                }
            }
        }

        assertEquals(Double.valueOf(111),
                simulationSet.getQuantity().getValue());
        assertEquals("poisson",
                simulationSet.getDistributionType().getValue());
        assertEquals(Double.valueOf(123),
                simulationSet.getUnitCost().getValue());
        assertEquals(Double.valueOf(999),
                simulationSet.getWorkingHours().getValue());
        assertEquals(Double.valueOf(321),
                simulationSet.getMean().getValue());
    }

    // TODO: Disabled @Test - As intermediate events has been disabled for M1
    @Ignore
    @SuppressWarnings("unchecked")
    public void testUnmarshallNotBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_NOT_BOUNDARY_EVENTS);
        assertEquals("Not Boundary Event",
                diagram.getMetadata().getTitle());
        assertDiagram(diagram,
                6);
        // Assert than the intermediate event is connected using a view connector,
        // so not boundary to the task ( not docked ).
        Node event = diagram.getGraph().getNode("_CB178D55-8DC2-4CAA-8C42-4F5028D4A1F6");
        List<Edge> inEdges = event.getInEdges();
        boolean foundViewConnector = false;
        for (Edge e : inEdges) {
            if (e.getContent() instanceof ViewConnector) {
                foundViewConnector = true;
            }
        }
        assertTrue(foundViewConnector);
        // Assert absolute position as the node is not docked.
        Bounds bounds = ((View) event.getContent()).getBounds();
        Bounds.Bound ul = bounds.getUpperLeft();
        Bounds.Bound lr = bounds.getLowerRight();
        assertEquals(305,
                ul.getX(),
                0);
        assertEquals(300,
                ul.getY(),
                0);
        assertEquals(335,
                lr.getX(),
                0);
        assertEquals(330,
                lr.getY(),
                0);
    }

    // TODO: Disabled @Test - As intermediate events has been disabled for M1
    @Ignore
    @SuppressWarnings("unchecked")
    public void testUnmarshallBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BOUNDARY_EVENTS);
        // Basic assertions.
        assertEquals("Boundary Event",
                diagram.getMetadata().getTitle());
        assertDiagram(diagram,
                6);
        // Assert than the intermediate event is connected using a dock connector,
        // so boundary to the task.
        Node event = diagram.getGraph().getNode("_CB178D55-8DC2-4CAA-8C42-4F5028D4A1F6");
        List<Edge> inEdges = event.getInEdges();
        boolean foundDockConector = false;
        for (Edge e : inEdges) {
            if (e.getContent() instanceof Dock) {
                foundDockConector = true;
            }
        }
        assertTrue(foundDockConector);
        // Assert relative position for the docked node.
        Bounds bounds = ((View) event.getContent()).getBounds();
        Bounds.Bound ul = bounds.getUpperLeft();
        Bounds.Bound lr = bounds.getLowerRight();
        assertEquals(57,
                ul.getX(),
                0);
        assertEquals(70,
                ul.getY(),
                0);
        assertEquals(87,
                lr.getX(),
                0);
        assertEquals(100,
                lr.getY(),
                0);
    }

    @Test
    public void testUnmarshallBusinessRuleTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKRULEFLOWGROUP);
        BusinessRuleTask businessRuleTask = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BusinessRuleTask) {
                    businessRuleTask = (BusinessRuleTask) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(businessRuleTask);
        assertNotNull(businessRuleTask.getExecutionSet());
        assertNotNull(businessRuleTask.getExecutionSet().getRuleFlowGroup());
        assertNotNull(businessRuleTask.getGeneral());
        assertNotNull(businessRuleTask.getGeneral().getName());
        assertEquals(businessRuleTask.getTaskType().getValue(),
                TaskTypes.BUSINESS_RULE);
        assertEquals("my business rule task",
                businessRuleTask.getGeneral().getName().getValue());
        assertEquals("my-ruleflow-group",
                businessRuleTask.getExecutionSet().getRuleFlowGroup().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallXorGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_XORGATEWAY);
        assertDiagram(diagram,
                7);
        assertEquals(diagram.getMetadata().getTitle(),
                "XORGateway");
        Graph graph = diagram.getGraph();
        Node<? extends Definition, ?> gatewayNode = graph.getNode("_877EA035-1A14-42E9-8CAA-43E9BF908C70");
        ExclusiveDatabasedGateway xorGateway = (ExclusiveDatabasedGateway) gatewayNode.getContent().getDefinition();
        assertEquals("AgeSplit",
                xorGateway.getGeneral().getName().getValue());
        assertEquals("under 10 : _5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD",
                xorGateway.getExecutionSet().getDefaultRoute().getValue());
        SequenceFlow sequenceFlow1 = null;
        SequenceFlow sequenceFlow2 = null;
        List<Edge> outEdges = (List<Edge>) gatewayNode.getOutEdges();
        if (outEdges != null) {
            for (Edge edge : outEdges) {
                if ("_C72E00C3-70DC-4BC9-A08E-761B4263A239".equals(edge.getUUID())) {
                    sequenceFlow1 = (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition();
                } else if ("_5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD".equals(edge.getUUID())) {
                    sequenceFlow2 = (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition();
                }
            }
        }
        Node<? extends Definition, ?> sequenceFlowNode1 = graph.getNode("_C72E00C3-70DC-4BC9-A08E-761B4263A239");
        assertNotNull(sequenceFlow1);
        assertEquals("10 and over",
                sequenceFlow1.getGeneral().getName().getValue());
        assertNotNull(sequenceFlow2);
        assertEquals("under 10",
                sequenceFlow2.getGeneral().getName().getValue());
    }

    @Test
    public void testUnmarshallReusableSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REUSABLE_SUBPROCESS);
        ReusableSubprocess reusableSubprocess = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ReusableSubprocess) {
                    reusableSubprocess = (ReusableSubprocess) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(reusableSubprocess);
        assertNotNull(reusableSubprocess.getExecutionSet());
        assertNotNull(reusableSubprocess.getExecutionSet().getCalledElement());
        assertNotNull(reusableSubprocess.getGeneral());
        assertNotNull(reusableSubprocess.getGeneral().getName());
        assertEquals("my subprocess",
                reusableSubprocess.getGeneral().getName().getValue());
        assertEquals("my-called-element",
                reusableSubprocess.getExecutionSet().getCalledElement().getValue());

        String assignmentsInfo = reusableSubprocess.getDataIOSet().getAssignmentsinfo().getValue();
        assertEquals("|input1:String,input2:Float||output1:String,output2:Float|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv1,[dout]output2->pv2",
                assignmentsInfo);
    }

    @Test
    public void testUnmarshallSeveralDiagrams() throws Exception {
        Diagram<Graph, Metadata> diagram1 = unmarshall(BPMN_EVALUATION);
        assertDiagram(diagram1,
                8);
        assertEquals("Evaluation",
                diagram1.getMetadata().getTitle());
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_LANES);
        assertDiagram(diagram,
                7);
        assertEquals("Lanes test",
                diagram.getMetadata().getTitle());
    }

    @Test
    public void testMarshallBasic() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BASIC);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                3,
                2);
    }

    @Test
    public void testMarshallEvaluation() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVALUATION);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                7,
                7);
    }

    // TODO: Disabled @Test - As intermediate events has been disabled for M1
    @Ignore
    public void testMarshallNotBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_NOT_BOUNDARY_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                5,
                4);
    }

    // TODO: Disabled @Test - As intermediate events has been disabled for M1
    @Ignore
    public void testMarshallBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BOUNDARY_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                5,
                3);
    }

    @Test
    public void testMarshallProcessVariables() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSVARIABLES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                7,
                7);
        assertTrue(result.contains("<bpmn2:itemDefinition id=\"_employeeItem\" structureRef=\"java.lang.String\"/>"));
        assertTrue(result.contains("<bpmn2:itemDefinition id=\"_reasonItem\" structureRef=\"java.lang.String\"/>"));
        assertTrue(result.contains("<bpmn2:itemDefinition id=\"_performanceItem\" structureRef=\"java.lang.String\"/>"));
        assertTrue(result.contains("<bpmn2:property id=\"employee\" itemSubjectRef=\"_employeeItem\"/>"));
        assertTrue(result.contains("<bpmn2:property id=\"reason\" itemSubjectRef=\"_reasonItem\"/>"));
        assertTrue(result.contains("<bpmn2:property id=\"performance\" itemSubjectRef=\"_performanceItem\"/>"));
    }

    @Test
    public void testMarshallProcessProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSPROPERTIES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                3,
                2);
        assertTrue(result.contains("bpmn2:process id=\"JDLProj.BPSimple\" drools:packageName=\"org.jbpm\" drools:version=\"1.0\" name=\"BPSimple\" isExecutable=\"true\""));
    }

    @Test
    public void testMarshallUserTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNMENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                7,
                7);
        assertTrue(result.contains("<bpmn2:dataInput id=\"_6063D302-9D81-4C86-920B-E808A45377C2_reasonInputX\" drools:dtype=\"com.test.Reason\" itemSubjectRef=\"__6063D302-9D81-4C86-920B-E808A45377C2_reasonInputXItem\" name=\"reason\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_6063D302-9D81-4C86-920B-E808A45377C2_performanceOutputX\" drools:dtype=\"Object\" itemSubjectRef=\"__6063D302-9D81-4C86-920B-E808A45377C2_performanceOutputXItem\" name=\"performance\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_6063D302-9D81-4C86-920B-E808A45377C2_performanceOutputX\" drools:dtype=\"Object\" itemSubjectRef=\"__6063D302-9D81-4C86-920B-E808A45377C2_performanceOutputXItem\" name=\"performance\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>reason</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_6063D302-9D81-4C86-920B-E808A45377C2_reasonInputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_6063D302-9D81-4C86-920B-E808A45377C2_performanceOutputX</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>performance</bpmn2:targetRef>"));
    }

    @Test
    public void testMarshallBusinessRuleTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKASSIGNMENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                3,
                2);
        assertTrue(result.contains("<bpmn2:dataInput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputXItem\" name=\"input1\"/>"));
        assertTrue(result.contains("<bpmn2:dataInput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputXItem\" name=\"input2\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputXItem\" name=\"output1\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputXItem\" name=\"output2\"/>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputX</bpmn2:dataOutputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputX</bpmn2:dataOutputRefs>"));
    }

    @Test
    public void testMarshallStartEventAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTEVENTASSIGNMENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                7,
                7);
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_106AADA7-E381-4736-8BD6-7EB47A1D096B_StartEventOutput1\" drools:dtype=\"String\" name=\"StartEventOutput1\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_106AADA7-E381-4736-8BD6-7EB47A1D096B_StartEventOutput1</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>employee</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_106AADA7-E381-4736-8BD6-7EB47A1D096B_StartEventOutput1</bpmn2:dataOutputRefs>"));
    }

    @Test
    public void testMarshallEndEventAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDEVENTASSIGNMENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                7,
                6);
        assertTrue(result.contains("<bpmn2:dataInput id=\"_0493ECBF-3C0F-4086-9979-2391CE740AA8_EndTermEventInput1InputX\" drools:dtype=\"String\" name=\"EndTermEventInput1\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>employee</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_0493ECBF-3C0F-4086-9979-2391CE740AA8_EndTermEventInput1InputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_0493ECBF-3C0F-4086-9979-2391CE740AA8_EndTermEventInput1InputX</bpmn2:dataInputRefs>"));

        assertTrue(result.contains("<bpmn2:dataInput id=\"_4824B9FD-6097-4E06-9E7E-2CE5E7601BC5_EndNoneEventInput1InputX\" drools:dtype=\"String\" name=\"EndNoneEventInput1\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>reason</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_4824B9FD-6097-4E06-9E7E-2CE5E7601BC5_EndNoneEventInput1InputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_4824B9FD-6097-4E06-9E7E-2CE5E7601BC5_EndNoneEventInput1InputX</bpmn2:dataInputRefs>"));
    }

    @Test
    public void testMarshallReusableSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REUSABLE_SUBPROCESS);
        assertDiagram(diagram,
                4);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                3,
                2);

        assertTrue(result.contains("<bpmn2:callActivity id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB\" name=\"my subprocess\" calledElement=\"my-called-element\">"));

        assertTrue(result.contains("<bpmn2:dataInput id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputX\" drools:dtype=\"String\" itemSubjectRef=\"__FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputXItem\" name=\"input1\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputX\" drools:dtype=\"Float\" itemSubjectRef=\"__FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputXItem\" name=\"output2\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>pv1</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputX</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>pv2</bpmn2:targetRef>"));
    }

    @Test
    public void testMarshallUserTaskAssignees() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNEES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                5,
                4);
        assertTrue(result.contains("<![CDATA[admin,kiemgmt]]>"));
        result = result.replace('\n',
                ' ');
        assertTrue(result.matches("(.*)<bpmn2:resourceAssignmentExpression(.*)>user</bpmn2:formalExpression>(.*)"));
        assertTrue(result.matches("(.*)<bpmn2:resourceAssignmentExpression(.*)>user1</bpmn2:formalExpression>(.*)"));
    }

    @Test
    public void testMarshallUserTaskProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKPROPERTIES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                3,
                2);
        assertTrue(result.contains("MyUserTask</bpmn2:from>"));
    }

    @Test
    public void testMarshallSimulationProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SIMULATIONPROPERTIES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                3,
                2);

        result = result.replaceAll("\\s+",
                " ");
        result = result.replaceAll("> <",
                "><");
        assertTrue(result.contains("<bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\"><bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\"><bpsim:PoissonDistribution mean=\"321.0\"/>"));
        assertTrue(result.contains("<bpsim:ResourceParameters xsi:type=\"bpsim:ResourceParameters\"><bpsim:Availability xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"999.0\"/>"));
        assertTrue(result.contains("<bpsim:Quantity xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"111.0\"/></bpsim:Quantity>"));
        assertTrue(result.contains("<bpsim:CostParameters xsi:type=\"bpsim:CostParameters\"><bpsim:UnitCost xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"123.0\"/>"));
        assertTrue(result.contains("<bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\"><bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\"><bpsim:UniformDistribution max=\"10.0\" min=\"5.0\"/>"));
    }

    @Test
    public void testMarshallEvaluationTwice() throws Exception {
        Diagram diagram = unmarshall(BPMN_EVALUATION);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                7,
                7);
        Diagram diagram2 = unmarshall(BPMN_EVALUATION);
        String result2 = tested.marshall(diagram2);
        assertDiagram(result2,
                1,
                7,
                7);
    }

    @Test
    public void testMarshallScriptTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SCRIPTTASK);
        ScriptTask scriptTask = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ScriptTask) {
                    scriptTask = (ScriptTask) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(scriptTask);
        assertNotNull(scriptTask.getExecutionSet());
        assertNotNull(scriptTask.getExecutionSet().getScript());
        assertNotNull(scriptTask.getExecutionSet().getScriptLanguage());
        assertNotNull(scriptTask.getGeneral());
        assertNotNull(scriptTask.getGeneral().getName());
        assertEquals(scriptTask.getTaskType().getValue(),
                TaskTypes.SCRIPT);
        assertEquals("my script task",
                scriptTask.getGeneral().getName().getValue());
        assertEquals("System.out.println(\"hello\");",
                scriptTask.getExecutionSet().getScript().getValue());
        assertEquals("java",
                scriptTask.getExecutionSet().getScriptLanguage().getValue());
    }

    @Test
    public void testMarshallSequenceFlow() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SEQUENCEFLOW);
        SequenceFlow sequenceFlow = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BusinessRuleTask) {
                    sequenceFlow = (SequenceFlow) ((ViewConnectorImpl) ((EdgeImpl) ((NodeImpl) element).getOutEdges().get(0)).getContent()).getDefinition();
                    break;
                }
            }
        }
        assertNotNull(sequenceFlow);
        assertNotNull(sequenceFlow.getExecutionSet());
        assertNotNull(sequenceFlow.getExecutionSet().getConditionExpression());
        assertNotNull(sequenceFlow.getExecutionSet().getConditionExpressionLanguage());
        assertNotNull(sequenceFlow.getExecutionSet().getPriority());
        assertNotNull(sequenceFlow.getGeneral());
        assertNotNull(sequenceFlow.getGeneral().getName());
        assertEquals("my sequence flow",
                sequenceFlow.getGeneral().getName().getValue());
        assertEquals("System.out.println(\"hello\");",
                sequenceFlow.getExecutionSet().getConditionExpression().getValue());
        assertEquals("java",
                sequenceFlow.getExecutionSet().getConditionExpressionLanguage().getValue());
        assertEquals("1",
                sequenceFlow.getExecutionSet().getPriority().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallBusinessRuleTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKRULEFLOWGROUP);
        String result = tested.marshall(diagram);
        assertDiagram(diagram,
                2);

        assertTrue(result.contains("<bpmn2:businessRuleTask id=\"_16D006B5-3703-4A67-AE44-6483338E86C2\" drools:ruleFlowGroup=\"my-ruleflow-group\" name=\"my business rule task\">"));
    }

    @Test
    public void testMarshallXorGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_XORGATEWAY);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                1,
                6,
                5);
        assertTrue(result.contains("<bpmn2:exclusiveGateway id=\"_877EA035-1A14-42E9-8CAA-43E9BF908C70\" drools:dg=\"under 10 : _5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD\" name=\"AgeSplit\" gatewayDirection=\"Diverging\" default=\"_5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD\">"));
    }

    // TODO: Disabled @Test - As intermediate events has been disabled for M1
    @Ignore
    public void testMarshallIntermediateTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_TIMER_EVENT);
        IntermediateTimerEvent timerEvent = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof IntermediateTimerEvent) {
                    timerEvent = (IntermediateTimerEvent) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(timerEvent);
        assertNotNull(timerEvent.getGeneral());
        assertNotNull(timerEvent.getExecutionSet());

        assertEquals("myTimeDateValue",
                timerEvent.getExecutionSet().getTimeDate().getValue());
        assertEquals("MyTimeDurationValue",
                timerEvent.getExecutionSet().getTimeDuration().getValue());
        assertEquals("myTimeCycleValue",
                timerEvent.getExecutionSet().getTimeCycle().getValue());
        assertEquals("cron",
                timerEvent.getExecutionSet().getTimeCycleLanguage().getValue());
    }

    private void assertDiagram(String result,
                               int diagramCount,
                               int nodeCount,
                               int edgeCount) {
        int d = count(result,
                "<bpmndi:BPMNDiagram");
        int n = count(result,
                "<bpmndi:BPMNShape");
        int e = count(result,
                "<bpmndi:BPMNEdge");
        assertEquals(diagramCount,
                d);
        assertEquals(nodeCount,
                n);
        assertEquals(edgeCount,
                e);
    }

    private void assertDiagram(Diagram<Graph, Metadata> diagram,
                               int nodesSize) {
        Graph graph = diagram.getGraph();
        assertNotNull(graph);
        Iterator<Node> nodesIterable = graph.nodes().iterator();
        List<Node> nodes = new ArrayList<>();
        nodesIterable.forEachRemaining(nodes::add);
        assertEquals(nodesSize,
                nodes.size());
    }

    private Diagram<Graph, Metadata> unmarshall(String fileName) throws Exception {
        InputStream is = loadStream(fileName);
        Metadata metadata =
                new MetadataImpl.MetadataImplBuilder(BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class)).build();
        DiagramImpl result = new DiagramImpl(org.kie.workbench.common.stunner.core.util.UUID.uuid(),
                metadata);
        Graph graph = tested.unmarshall(metadata,
                is);
        result.setGraph(graph);
        // Update diagram's  metadata.
        tested.updateRootUUID(result.getMetadata(),
                graph);
        updateTitle(result.getMetadata(),
                graph);
        return result;
    }

    @SuppressWarnings("unchecked")
    private Iterator<Element> nodesIterator(Diagram<Graph, Metadata> diagram) {
        return (Iterator<Element>) diagram.getGraph().nodes().iterator();
    }

    private void updateTitle(final Metadata metadata,
                             final Graph graph) {
        // Update metadata's title.
        final String title = tested.getTitle(graph);
        metadata.setTitle(title);
    }

    private InputStream loadStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    private static int count(final String string,
                             final String substring) {
        int count = 0;
        int idx = 0;
        while ((idx = string.indexOf(substring,
                idx)) != -1) {
            idx++;
            count++;
        }
        return count;
    }
}