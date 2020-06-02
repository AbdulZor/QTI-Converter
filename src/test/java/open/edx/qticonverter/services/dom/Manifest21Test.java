package open.edx.qticonverter.services.dom;

import open.edx.qticonverter.models.qti.manifest.Manifest;
import open.edx.qticonverter.models.qti.manifest.Manifest21Builder;
import open.edx.qticonverter.models.qti.manifest.ManifestBuilder;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Manifest21Test {
    private static final String XMLNS = "http://www.imsglobal.org/xsd/imscp_v1p1";
    private static final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_SCHEMA_LOCATION = "http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd " +
            "http://www.imsglobal.org/xsd/imsmd_v1p2 imsmd_v1p2p4.xsd http://www.imsglobal.org/xsd/imsqti_metadata_v2p1  " +
            "http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_metadata_v2p1.xsd";


    ManifestBuilder manifestBuilder1;
    ManifestBuilder manifestBuilder2;

    @BeforeEach
    void setUp() {
        this.manifestBuilder1 = new Manifest21Builder();
        this.manifestBuilder2 = new Manifest21Builder();
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
        expectedManifestElement.setNamespace(Namespace.getNamespace(XMLNS));

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
}
