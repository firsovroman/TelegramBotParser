package com.roman.telegram_bot_parser.utils;

import com.roman.telegram_bot_parser.dao.Ads;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParsingUtils {

    private static final String URL_MAKER = "https://www.avito.ru";

    public static List<Ads> parseToList(Document page) {
        Element firstElement = page.select("div[class=items-items-kAJAg][data-marker=catalog-serp]").first();
        Elements newNames  = firstElement.getElementsByAttributeValue("data-marker", "item");

        List<Ads> adsList = new ArrayList<>();
        for(Element e : newNames) {
            Ads one = new Ads(
                    Long.valueOf(e.attr("data-item-id")),
                    URL_MAKER + e.getElementsByAttributeValue("itemprop","url").attr("href"),
                    e.getElementsByAttributeValue("itemprop","price").attr("content"),
                    e.getElementsByAttributeValue("data-marker","item-date").text()
            );
            adsList.add(one);
        }
        return adsList;
    }


    public static HashMap<String , Ads> parseToMap(Document page) {

        Element firstElement = page.select("div[class=items-items-kAJAg][data-marker=catalog-serp]").first();
        Elements newNames  = firstElement.getElementsByAttributeValue("data-marker", "item");

        HashMap<String , Ads> entityHashMap = new HashMap<>();
        for(Element e : newNames) {
            Ads one = new Ads(
                    Long.valueOf(e.attr("data-item-id")),
                    URL_MAKER + e.getElementsByAttributeValue("itemprop","url").attr("href"),
                    e.getElementsByAttributeValue("itemprop","price").attr("content"),
                    e.getElementsByAttributeValue("data-marker","item-date").text()
            );
            entityHashMap.put(e.attr("data-item-id") ,one);
        }
        return entityHashMap;
    }

}
