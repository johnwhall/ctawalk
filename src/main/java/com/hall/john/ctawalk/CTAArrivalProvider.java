package com.hall.john.ctawalk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
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

			XPath xp = XPathFactory.newInstance().newXPath();

			String errCd = xp.evaluate("/ctatt/errCd", doc);
			if (!errCd.equals("0")) {
				new RuntimeException("Got error code: " + errCd).printStackTrace();
				return new int[0];
			}

			NodeList etaNodes = (NodeList) xp.evaluate("/ctatt/eta", doc, XPathConstants.NODESET);
			for (Node etaNode : iterableNodeList(etaNodes)) {
				String prdtString = xp.evaluate("prdt", etaNode);
				Date prdt = _dateFormat.parse(prdtString);

				String arrTString = xp.evaluate("arrT", etaNode);
				Date arrT = _dateFormat.parse(arrTString);

				int diffSecs = (int) TimeUnit.MILLISECONDS.toSeconds(arrT.getTime() - prdt.getTime());
				arrivals.add(diffSecs);
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

	private static List<Node> iterableNodeList(NodeList nodeList) {
		List<Node> ret = new ArrayList<Node>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			ret.add(nodeList.item(i));
		}

		return ret;
	}

}
