package com.akbank.camel.soap;

import java.util.List;
import java.util.ArrayList;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfPayload;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.cxf.binding.soap.SoapHeader;
import org.springframework.stereotype.Component;

@Component
public class SampleRoute extends RouteBuilder {
    protected final String simpleEndpointAddress = "/" + getClass().getSimpleName();
    protected final String simpleEndpointURI = "cxf://" + simpleEndpointAddress
            + "?serviceClass=com.akbank.camel.service.EchoService";

    private static final String ECHO_METHOD = "ns1:echo xmlns:ns1=\"http://jaxws.cxf.component.camel.apache.org/\"";

    private static final String ECHO_RESPONSE = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<soap:Body><ns1:echoResponse xmlns:ns1=\"http://jaxws.cxf.component.camel.apache.org/\">"
            + "<return xmlns=\"http://jaxws.cxf.component.camel.apache.org/\">echo Hello World!</return>"
            + "</ns1:echoResponse></soap:Body></soap:Envelope>";
    private static final String ECHO_BOOLEAN_RESPONSE = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<soap:Body><ns1:echoBooleanResponse xmlns:ns1=\"http://jaxws.cxf.component.camel.apache.org/\">"
            + "<return xmlns=\"http://jaxws.cxf.component.camel.apache.org/\">true</return>"
            + "</ns1:echoBooleanResponse></soap:Body></soap:Envelope>";

    protected static final String ELEMENT_NAMESPACE = "http://service.camel.akbank.com/";

    @Override
    public void configure() throws Exception {
        
        
        /*
        **** The below example is to show dataformat use.******
        *********************************************************
        
        from(simpleEndpointURI + "&dataFormat=PAYLOAD").to("log:info").process(new Processor() {
            @SuppressWarnings("unchecked")
            public void process(final Exchange exchange) throws Exception {
                CxfPayload<SoapHeader> requestPayload = exchange.getIn()
                    .getBody(CxfPayload.class);
                List<Source> inElements = requestPayload.getBodySources();
                List<Source> outElements = new ArrayList<>();
                // You can use a customer toStringConverter to turn a CxfPayLoad message into
                // String as you want
                String request = exchange.getIn().getBody(String.class);
                XmlConverter converter = new XmlConverter();
                String documentString = ECHO_RESPONSE;

                Element in = new XmlConverter().toDOMElement(inElements.get(0));
                // Just check the element namespace
                if (!in.getNamespaceURI().equals(ELEMENT_NAMESPACE)) {
                    throw new IllegalArgumentException("Wrong element namespace");
                }
                if (in.getLocalName().equals("echoBoolean")) {
                    documentString = ECHO_BOOLEAN_RESPONSE;
                } else {
                    documentString = ECHO_RESPONSE;
                }
                Document outDocument = converter.toDOMDocument(documentString, exchange);
                outElements.add(new DOMSource(outDocument.getDocumentElement()));
                // set the payload header with null
                CxfPayload<SoapHeader> responsePayload = new CxfPayload<>(null, outElements,
                                                                          null);
                exchange.getMessage().setBody(responsePayload);
            }
        });
        */

        from(simpleEndpointURI).routeId("cxfEndPointEx").to("log:info").choice()
        .when().simple("${body} == 'mervan'")
           .convertBodyTo(String.class)
           .transform(simple("${body.toUpperCase}"))
           .log(">> Header : ${headers}").endChoice()
      .end().transform(simple("Hi ${body}"));
       
    }

}
