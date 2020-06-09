package open.edx.qticonverter.services.dom;

import open.edx.qticonverter.models.qti.manifest.Manifest;
import open.edx.qticonverter.models.qti.manifest.Manifest21Builder;
import open.edx.qticonverter.models.qti.manifest.ManifestBuilder;
import open.edx.qticonverter.models.qti.manifest.enums.SchemaVersion;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Manifest21Test {
    private static final String XMLNS = "http://www.imsglobal.org/xsd/imscp_v1p1";
    private static final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_SCHEMA_LOCATION = "http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd " +
            "http://www.imsglobal.org/xsd/imsmd_v1p2 imsmd_v1p2p4.xsd http://www.imsglobal.org/xsd/imsqti_metadata_v2p1  " +
            "http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_metadata_v2p1.xsd";
    private static final Namespace XMLNS_OBJECT = Namespace.getNamespace(XMLNS);

    ManifestBuilder manifestBuilder1;
    ManifestBuilder manifestBuilder2;

    @BeforeEach
    void setUp() {
        this.manifestBuilder1 = new Manifest21Builder();
        this.manifestBuilder2 = new Manifest21Builder();

        this.manifestBuilder2.initializeDocument();
    }

    @AfterEach
    void tearDown() {
        this.manifestBuilder1 = null;
        this.manifestBuilder2 = null;
    }

    @Test
    void initializeDocument_Should_Create_Document() {
        this.manifestBuilder1.initializeDocument();
        Manifest manifest = this.manifestBuilder1.getResult();

        Document actualDocument = manifest.getDocument();
        Document expectedDocument = new Document();
        assertEquals(actualDocument.getClass(), expectedDocument.getClass());
    }

    @Test
    void initializeDocument_Should_Create_Document_With_Manifest_As_Root() {
        this.manifestBuilder1.initializeDocument();
        Manifest manifest = this.manifestBuilder1.getResult();

        Document actualDocument = manifest.getDocument();

        // expected results
        Element expectedManifestElement = new Element("manifest", XMLNS);
        expectedManifestElement.setNamespace(XMLNS_OBJECT);

        Namespace xsi = Namespace.getNamespace("xsi", XMLNS_XSI);
        expectedManifestElement.addNamespaceDeclaration(xsi);
        expectedManifestElement.setAttribute("schemaLocation", XSI_SCHEMA_LOCATION, xsi);

        Document expectedDocument = new Document(expectedManifestElement);

        assertEquals(expectedDocument.getRootElement().getName(), actualDocument.getRootElement().getName(), "The root elements name do not match");
        // check if nameSpaces are assigned correctly
        assertEquals(expectedDocument.getRootElement().getNamespace(), actualDocument.getRootElement().getNamespace(), "The root element default namespace do not match");
        assertEquals(expectedDocument.getRootElement().getNamespace(xsi.getPrefix()), actualDocument.getRootElement().getNamespace(xsi.getPrefix()));
        assertEquals(expectedDocument.getRootElement().getAttributeValue("schemaLocation"), actualDocument.getRootElement().getAttributeValue("schemaLocation"));
    }

    @Test
    void initializeDocument_Should_CreateRootElements() {
        this.manifestBuilder1.initializeDocument();
        Manifest manifest = this.manifestBuilder1.getResult();

        // Expected root elements
        Element expectedMetaDataElement = new Element("metadata");
        Element expectedResourcesElement = new Element("resources");
        Element expectedOrganizationElement = new Element("organizations");

        assertEquals(expectedMetaDataElement.getName(), manifest.getRootMetadataElement().getName());
        assertEquals(expectedResourcesElement.getName(), manifest.getRootResourcesElement().getName());
        assertEquals(expectedOrganizationElement.getName(), manifest.getRootOrganizationsElement().getName());
    }

    @Test
    void setMetadata_Should_Create_Schema_SchemaVersion_Elements() {
        this.manifestBuilder2.setMetadata("IMS Content", SchemaVersion.V11);

        Manifest manifest = this.manifestBuilder2.getResult();

        Element schema = manifest.getRootMetadataElement().getChild("schema", XMLNS_OBJECT);
        Element schemaVersion = manifest.getRootMetadataElement().getChild("schemaversion", XMLNS_OBJECT);

        assertNotEquals("", schema);
        assertNotNull(schemaVersion);

        assertEquals("schema", schema.getName());
        assertEquals("schemaversion", schemaVersion.getName());
    }

    @Test
    void setMetadata_Should_Create_Correct_Schema_SchemaVersion_Elements() {
        String expectedSchema = "IMS Content";
        SchemaVersion expectedSchemaVersion = SchemaVersion.V11;

        this.manifestBuilder2.setMetadata("IMS Content", SchemaVersion.V11);

        Manifest manifest = this.manifestBuilder2.getResult();

        Element schema = manifest.getRootMetadataElement().getChild("schema", XMLNS_OBJECT);
        Element schemaVersion = manifest.getRootMetadataElement().getChild("schemaversion", XMLNS_OBJECT);

        assertEquals(expectedSchema, schema.getText());
        assertEquals(expectedSchemaVersion.name(), schemaVersion.getText());
    }

    @Test
    void setMetadata_Should_Use_Defaults_When_Schema_And_Schemaversion_Is_Undefined() {
        String actualSchema = null;
        SchemaVersion actualSchemaVersion = null;

        this.manifestBuilder2.setMetadata(actualSchema, actualSchemaVersion);

        Manifest manifest = this.manifestBuilder2.getResult();

        String expectedSchema = Manifest21Builder.DEFAULT_SCHEMA;
        SchemaVersion expectedSchemaVersion = Manifest21Builder.DEFAULT_SCHEMA_VERSION;

        Element schema = manifest.getRootMetadataElement().getChild("schema", XMLNS_OBJECT);
        Element schemaVersion = manifest.getRootMetadataElement().getChild("schemaversion", XMLNS_OBJECT);

        assertEquals(expectedSchema, schema.getText());
        assertEquals(expectedSchemaVersion.name(), schemaVersion.getText());
    }

    @Test
    void addResource_Should_Create_ResourceElement_With_Attributes() {
        String expectedIdentifier = "CheckBox-1";
        String expectedType = "imsqti_item_xmlv2p1";
        String expectedHref = "Checkbox-1.xml";
        List<String> expectedDependencyList = null;

        this.manifestBuilder2.addResource(expectedIdentifier, expectedType, expectedHref, expectedDependencyList);

        Manifest manifest = this.manifestBuilder2.getResult();

        String identifier = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getAttributeValue("identifier");
        String type = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getAttributeValue("type");
        String href = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getAttributeValue("href");

        assertEquals(expectedIdentifier, identifier);
        assertEquals(expectedType, type);
        assertEquals(expectedHref, href);
    }

    @Test
    void addResource_Should_Create_ResourceElement_With_Attributes_And_Dependencies() {
        String expectedIdentifier = "CheckBox-1";
        String expectedType = "imsqti_item_xmlv2p1";
        String expectedHref = "Checkbox-1.xml";
        List<String> expectedDependencyList = new ArrayList<>();
        expectedDependencyList.add("Dog.jpeg");
        expectedDependencyList.add("Checkbox.css");

        this.manifestBuilder2.addResource(expectedIdentifier, expectedType, expectedHref, expectedDependencyList);

        Manifest manifest = this.manifestBuilder2.getResult();

        String identifier = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getAttributeValue("identifier");
        String type = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getAttributeValue("type");
        String href = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getAttributeValue("href");

        assertEquals(expectedIdentifier, identifier);
        assertEquals(expectedType, type);
        assertEquals(expectedHref, href);

        List<Element> dependencies = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getChildren("dependency", XMLNS_OBJECT);

        for (Element dependency : dependencies) {
            String actualIdentifierref = dependency.getAttributeValue("identifierref");
            assertTrue(expectedDependencyList.contains(actualIdentifierref));
        }
    }

    @Test
    void addResource_Should_Create_FileElement_With_Correct_Href_Attribute() {
        String expectedIdentifier = "CheckBox-1";
        String expectedType = "imsqti_item_xmlv2p1";
        String expectedHref = "Checkbox-1.xml";
        List<String> expectedDependencyList = null;
        Element expectedFileElement = new Element("file");

        this.manifestBuilder2.addResource(expectedIdentifier, expectedType, expectedHref, expectedDependencyList);
        Manifest manifest = this.manifestBuilder2.getResult();

        String href = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getAttributeValue("href");
        Element fileElement = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getChild("file", XMLNS_OBJECT);
        String fileHref = manifest.getRootResourcesElement().getChild("resource", XMLNS_OBJECT).getChild("file", XMLNS_OBJECT).getAttributeValue("href");

        assertEquals(expectedHref, href);
        assertEquals(expectedHref, fileHref);
        assertEquals(expectedFileElement.getName(), fileElement.getName());
    }

    @Test
    void addResource_Should_Throw_When_Identifier_AndOr_Href_Is_NullOrUndefined() {
        String expectedIdentifier = "CheckBox-1";
        String expectedType = "imsqti_item_xmlv2p1";
        String expectedHref = "Checkbox-1.xml";
        List<String> expectedDependencyList = null;

        assertThrows(NullPointerException.class, () -> {
            this.manifestBuilder2.addResource(null, expectedType, expectedHref, expectedDependencyList);
        });

        assertThrows(NullPointerException.class, () -> {
            this.manifestBuilder2.addResource(expectedIdentifier, expectedType, null, expectedDependencyList);
        });

        assertThrows(NullPointerException.class, () -> {
            this.manifestBuilder2.addResource(null, expectedType, null, expectedDependencyList);
        });
    }

    @Test
    void getResult_Should_Return_Manifest_Object() {
        Manifest manifest = this.manifestBuilder1.getResult();
        assertNotNull(manifest);
    }
}
