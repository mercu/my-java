package com.mercu.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class WebDomService {

    public Elements elements(String html, String selector) {
        return Jsoup.parse(html)
                .select(selector);
    }

    public Element element(String html, String selector) {
        return Jsoup.parse(html)
                .select(selector)
                .first();
    }
}
