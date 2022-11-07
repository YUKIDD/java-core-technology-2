package org.example.chap03.xpath;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogManager;
import javax.xml.catalog.CatalogResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class XPathTest {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        builder.setEntityResolver(CatalogManager.catalogResolver(CatalogFeatures.defaults(),
                Paths.get("src/main/java/org/example/chap03/xpath/catalog.xml").toAbsolutePath().toUri()));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath path = xPathFactory.newXPath();
        try (Scanner in = new Scanner(System.in)) {
            String filename;
            if (args.length == 0) {
                System.out.print("Input file:");
                filename = in.nextLine();
            } else {
                filename = args[0];
            }

            Document doc = builder.parse(filename);
            boolean done = false;
            while (!done) {
                System.out.print("XPath expression (empty line to exit): ");
                String expression = in.nextLine();
                if (expression.trim().isEmpty()) {
                    done = true;
                } else {
                    try {
                        XPathEvaluationResult<?> result = path.evaluateExpression(expression,doc);
                        if (result.type() == XPathEvaluationResult.XPathResultType.NODESET) {
                            for (Node n : (XPathNodes) result.value()) {
                                System.out.println(description(n));
                            }
                        } else if (result.type() == XPathEvaluationResult.XPathResultType.NODE) {
                            System.out.println((Node) result.value());
                        } else if (result.type() == XPathEvaluationResult.XPathResultType.ANY) {
                            System.out.println((Node)result.value());
                        } else {
                            System.out.println(result.value());
                        }
                    } catch (XPathExpressionException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    public static String description(Node n) {
        if (n instanceof Element) {
            return "Element " + n.getNodeName();
        } else if (n instanceof Attr) {
            return "Attribute " + n;
        } else {
            return n.toString();
        }
    }
}
