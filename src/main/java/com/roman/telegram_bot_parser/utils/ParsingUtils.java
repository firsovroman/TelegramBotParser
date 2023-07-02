package com.roman.telegram_bot_parser.utils;

import com.roman.telegram_bot_parser.dao.Ad;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParsingUtils {

    private static final String URL_MAKER = "https://www.avito.ru";

    public static List<Ad> parseToList(Document page) {
        Element firstElement = page.select("div[class=items-items-kAJAg][data-marker=catalog-serp]").first();
        Elements newNames = firstElement.getElementsByAttributeValue("data-marker", "item");

        List<Ad> adList = new ArrayList<>();
        for (Element e : newNames) {
            Ad one = new Ad(
                    Long.parseLong(e.attr("data-item-id")),
                    URL_MAKER + e.getElementsByAttributeValue("itemprop", "url").attr("href"),
                    e.getElementsByAttributeValue("itemprop", "price").attr("content"),
                    e.getElementsByAttributeValue("data-marker", "item-date").text(),
                    e.select("meta[itemprop=description]").attr("content")
            );
            adList.add(one);
        }
        return adList;
    }


    public static HashMap<String, Ad> parseToMap(Document page) {

        Element firstElement = page.select("div[class=items-items-kAJAg][data-marker=catalog-serp]").first();
        Elements newNames = firstElement.getElementsByAttributeValue("data-marker", "item");

        HashMap<String, Ad> entityHashMap = new HashMap<>();
        for (Element e : newNames) {
            Ad one = new Ad(
                    Long.parseLong(e.attr("data-item-id")),
                    URL_MAKER + e.getElementsByAttributeValue("itemprop", "url").attr("href"),
                    e.getElementsByAttributeValue("itemprop", "price").attr("content"),
                    e.getElementsByAttributeValue("data-marker", "item-date").text(),
                    e.select("meta[itemprop=description]").attr("content")
            );
            entityHashMap.put(e.attr("data-item-id"), one);
        }
        return entityHashMap;
    }

}
