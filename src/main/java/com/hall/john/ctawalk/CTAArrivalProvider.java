package com.hall.john.ctawalk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hall.john.ctawalk.settings.ISettings;

@Component
public class CTAArrivalProvider implements IArrivalsProvider {

	private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

	@Autowired
	private ISettings _settings;

	private String _url;
	private Logger _logger = LoggerFactory.getLogger(getClass());

	@PostConstruct
	public void postConstruct() {
		_url = String.format("http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?key=%s&stpid=%s&rt=%s",
				_settings.getAPIKey(), _settings.getStopID(), _settings.getRouteCode());

		_logger.info("URL: " + _url);
	}

	@Override
	public int[] getArrivals() {
		List<Integer> arrivals = new ArrayList<Integer>();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(_url);

			Element ctAtt = doc.getDocumentElement();

			String errCd = getChildTextContent(ctAtt, "errCd");
			if (!errCd.equals("0")) {
				new RuntimeException("Got error code: " + errCd).printStackTrace();
				return new int[0];
			}

			for (Element etaElem : childrenList(ctAtt, (x) -> x.equals("eta"))) {
				String prdtString = getChildTextContent(etaElem, "prdt");
				Date prdt = _dateFormat.parse(prdtString);

				String arrTString = getChildTextContent(etaElem, "arrT");
				Date arrT = _dateFormat.parse(arrTString);

				int diffMins = (int) ((arrT.getTime() - prdt.getTime()) / (1000 * 60));
				arrivals.add(diffMins);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		int[] ret = new int[arrivals.size()];
		for (int i = 0; i < arrivals.size(); i++) {
			ret[i] = arrivals.get(i);
		}

		return ret;
	}

	private static List<Element> childrenList(Element e, Predicate<String> filter) {
		List<Element> retChildren = new ArrayList<Element>();
		NodeList nodeList = e.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				if (filter == null || filter.test(elem.getTagName())) {
					retChildren.add(elem);
				}
			}

		}

		return retChildren;
	}

	private static String getChildTextContent(Element e, String childTagName) {
		for (Element child : childrenList(e, null)) {
			if (child.getTagName().equals(childTagName)) {
				return child.getTextContent();
			}
		}

		return null;
	}

}
