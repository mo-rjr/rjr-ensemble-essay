package uk.gov.metoffice.hello.gatekeeper;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.gov.metoffice.hello.message.Ensemble;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class EnsembleDataReader {

    private static final String SURFACE_RUNOFF_NODES_XPATH = "//mapStack[parameterId=\"surfacerunoff\"]";
    private static final String RAINFALL_NODES_XPATH = "//mapStack[parameterId=\"Precipitation\"]";
    private static final String DATE_XPATH = "startDate/@date";
    private static final String TIME_XPATH = "startDate/@time";
    private static final String BIL_FILE_XPATH = "file/usgs/@file";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_TIME;

    private final XPathExpression surfaceRunoffNodesExpression;
    private final XPathExpression rainfallNodesExpression;
    private final XPathExpression dateExpression;
    private final XPathExpression timeExpression;
    private final XPathExpression bilFileExpression;

    private EnsembleDataReader(XPathExpression surfaceRunoffNodesExpression,
                               XPathExpression rainfallNodesExpression,
                               XPathExpression dateExpression,
                               XPathExpression timeExpression,
                               XPathExpression bilFileExpression) {
        this.surfaceRunoffNodesExpression = surfaceRunoffNodesExpression;
        this.rainfallNodesExpression = rainfallNodesExpression;
        this.dateExpression = dateExpression;
        this.timeExpression = timeExpression;
        this.bilFileExpression = bilFileExpression;
    }


    public static EnsembleDataReader create()  {

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression surfaceRunoffNodesExpression = xPath.compile(SURFACE_RUNOFF_NODES_XPATH);
            XPathExpression rainfallNodesExpression = xPath.compile(RAINFALL_NODES_XPATH);
            XPathExpression dateExpression = xPath.compile(DATE_XPATH);
            XPathExpression timeExpression = xPath.compile(TIME_XPATH);
            XPathExpression bilFileExpression = xPath.compile(BIL_FILE_XPATH);
            return new EnsembleDataReader(surfaceRunoffNodesExpression,
                    rainfallNodesExpression, dateExpression, timeExpression,
                    bilFileExpression);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Cannot create EnsembleDataReader: check XPathExpression values in code");
        }

    }
    public Ensemble readFromXmlFile(String sourceZipName, String dataRoot, String ensembleXml) {
//        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ensembleXml)) {

        try (InputStream inputStream = new FileInputStream(dataRoot + ensembleXml)) {
            Document document = setUpXmlDocument(inputStream);
            int ensembleId = fetchIdFromEnsembleXml(ensembleXml);

            Map<ZonedDateTime, String> runoffFilePerTimestep = extractTimesToFileNames(document, dataRoot);
            String rainfallFile = extractRainfallFile(document);
            return new Ensemble(sourceZipName, ensembleId, runoffFilePerTimestep, rainfallFile);
        } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
            throw new RuntimeException(e);
            // TODO handle exceptions properly -- I've collapsed them all, which isn't wise
        }
    }

    private String extractRainfallFile(Document document) throws XPathExpressionException {
        NodeList nodeList = (NodeList) rainfallNodesExpression.evaluate(document, XPathConstants.NODESET);
        if (nodeList.getLength() < 1) {
            throw new RuntimeException("No rainfall file found");
            // better handling
        }
        Node node = nodeList.item(0);
        return bilFileExpression.evaluate(node);
    }

    private int fetchIdFromEnsembleXml(String ensembleXml) {
        int fileNameLength = ensembleXml.length();
        String number = ensembleXml.substring(fileNameLength-6, fileNameLength-4);
        /////// parse
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0;
            // todo obviously this is short-term code
        }
    }

//    private Map<ZonedDateTime, String> readDocument(String docXml) {
//        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(docXml)) {
//            if (inputStream == null) {
//                throw new RuntimeException("You're not reading file OK");
//            }
//
//            Document document = setUpXmlDocument(inputStream);
//            NodeList nodeList = (NodeList) surfaceRunoffNodesExpression.evaluate(document, XPathConstants.NODESET);
//            return extractTimesToFileNames(nodeList);
//
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        } catch (ParserConfigurationException | SAXException |XPathExpressionException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private Map<ZonedDateTime, String> extractTimesToFileNames(Document document, String dataRoot) throws XPathExpressionException {
        NodeList nodeList = (NodeList) surfaceRunoffNodesExpression.evaluate(document, XPathConstants.NODESET);
        int nodeCount = nodeList.getLength();
        Map<ZonedDateTime, String> timesToFileNames = new LinkedHashMap<>();

        for (int step = 0; step < nodeCount; step++) {
            Node node = nodeList.item(step);
            String date = dateExpression.evaluate(node);
            LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
            String time = timeExpression.evaluate(node);
            LocalTime localTime = LocalTime.parse(time, TIME_FORMATTER);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate, localTime, ZoneOffset.UTC);
            String fileName = bilFileExpression.evaluate(node);
            timesToFileNames.put(zonedDateTime, dataRoot + fileName);
        }
        return timesToFileNames;
    }

    private Document setUpXmlDocument(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        return builder.parse(inputStream);
    }

}
