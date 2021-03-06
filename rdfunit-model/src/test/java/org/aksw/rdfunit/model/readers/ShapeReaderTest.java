package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.PropertyConstraintGroup;
import org.aksw.rdfunit.model.interfaces.Shape;
import org.aksw.rdfunit.model.interfaces.ShapeTarget;
import org.aksw.rdfunit.model.shacl.TemplateRegistry;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 5/2/2016 4:42 μμ
 */
public class ShapeReaderTest {

    private static final String shapeResource = "/org/aksw/rdfunit/shacl/sampleShape.ttl" ;

    @Before
    public void setUp() throws RdfReaderException {
        // Needed to resolve the patterns
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }

    @Test
    public void testRead() throws RdfReaderException {

        Model shapesModel = RdfReaderFactory.createResourceReader(shapeResource).read();

        List<Shape> shapes = shapesModel.listResourcesWithProperty(RDF.type, SHACL.Shape).toList()
                .stream()
                .map( r -> ShapeReader.create(TemplateRegistry.createCore()).read(r))
                .collect(Collectors.toList());

        assertThat(shapes)
                .hasSize(1);

        Shape sh = shapes.get(0);

        checkTarget(sh);

        testPropertyGroups(sh);
    }

    private void testPropertyGroups(Shape sh) {
        assertThat(sh.getPropertyConstraintGroups())
                .hasSize(2);

        // have one normal & one inverse property
        assertThat(sh.getPropertyConstraintGroups().stream().map(PropertyConstraintGroup::isInverse).distinct().collect(Collectors.toList()))
                .hasSize(2);

        // make sure exact same with reverse
        //also verifies equality checks
        PropertyConstraintGroup pcg1 = sh.getPropertyConstraintGroups().get(0);
        PropertyConstraintGroup pcg2 = sh.getPropertyConstraintGroups().get(1);

        //assertThat(pcg1.getPropertyConstraints())
        //        .hasSize(23);

        assertThat(pcg1.getPropertyConstraints().size())
                .isEqualTo(pcg2.getPropertyConstraints().size());

        pcg1.getPropertyConstraints().forEach(p -> {
            assertThat(pcg2.getPropertyConstraints().contains(p));
            assertThat(p.getTestCase(pcg1.isInverse()).getLogLevel().equals(RLOGLevel.WARN));
        });
    }

    private void checkTarget(Shape sh) {
        assertThat(sh.getTargets())
                .hasSize(ShapeTargetType.values().length-1);


        List<ShapeTargetType> targetTypes = sh.getTargets().stream()
                .map(ShapeTarget::getTargetType)
                .distinct()
                .collect(Collectors.toList());

        // distinct targets
        assertThat(targetTypes)
                .hasSize(ShapeTargetType.values().length-1)
                ;
    }
}