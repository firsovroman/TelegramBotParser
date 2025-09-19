package com.roman.telegram_bot_parser.utils;

import com.roman.telegram_bot_parser.dao.Ad;
import com.roman.telegram_bot_parser.logic.ParserAdapter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParsingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingUtils.class);

    private static final String URL_MAKER = "https://www.avito.ru";
    
    // Fallback селекторы для повышения надежности парсинга
    private static final String[] CATALOG_SELECTORS = {
        "div[class=items-items-kAJAg][data-marker=catalog-serp]",
        "div[data-marker=catalog-serp]",
        ".items-items",
        "[data-marker=catalog-serp]"
    };
    
    private static final String[] PROMOTED_AD_SELECTORS = {
        ".styles-redesign-YLctS",
        "[data-marker=item-premium]",
        ".item-premium",
        ".premium"
    };

    public static List<Ad> parseToList(Document page) {
        if (page == null) {
            LOGGER.error("Document is null, cannot parse");
            return new ArrayList<>();
        }
        
        Element catalogElement = findCatalogElement(page);
        if (catalogElement == null) {
            LOGGER.error("Catalog element not found, unable to parse ads");
            return new ArrayList<>();
        }
        
        Elements adElements = catalogElement.getElementsByAttributeValue("data-marker", "item");
        if (adElements.isEmpty()) {
            LOGGER.warn("No ad elements found in catalog");
            return new ArrayList<>();
        }
        
        LOGGER.info("Found {} ad elements to process", adElements.size());
        
        List<Ad> adList = new ArrayList<>();
        int skippedCount = 0;
        int errorCount = 0;
        
        for (Element element : adElements) {
            try {
                // Проверяем является ли объявление промо
                if (isPromotedAd(element)) {
                    String adId = element.attr("data-item-id");
                    LOGGER.debug("Skipping promoted ad with id: {}", adId);
                    skippedCount++;
                    continue;
                }

                Ad ad = parseAdElement(element);
                if (ad != null) {
                    adList.add(ad);
                } else {
                    errorCount++;
                }
                
            } catch (Exception e) {
                LOGGER.error("Error parsing ad element: {}", e.getMessage(), e);
                errorCount++;
            }
        }
        
        LOGGER.info("Parsing completed: {} ads parsed, {} promoted ads skipped, {} errors", 
                   adList.size(), skippedCount, errorCount);
        return adList;
    }
    
    private static Element findCatalogElement(Document page) {
        for (String selector : CATALOG_SELECTORS) {
            Element element = page.select(selector).first();
            if (element != null) {
                LOGGER.debug("Found catalog using selector: {}", selector);
                return element;
            }
        }
        return null;
    }
    
    private static boolean isPromotedAd(Element element) {
        for (String selector : PROMOTED_AD_SELECTORS) {
            if (!element.select(selector).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    private static Ad parseAdElement(Element element) {
        try {
            String itemId = element.attr("data-item-id");
            if (itemId.isEmpty()) {
                LOGGER.warn("Ad element missing data-item-id attribute");
                return null;
            }
            
            Long id = Long.parseLong(itemId);
            
            String href = extractHref(element);
            if (href == null) {
                LOGGER.warn("Ad {} missing URL", id);
                return null;
            }
            String url = URL_MAKER + href;
            
            String price = extractPrice(element);
            String date = extractDate(element);
            String description = extractDescription(element);
            
            return new Ad(id, url, price, date, description);
            
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid ad ID format: {}", element.attr("data-item-id"));
            return null;
        } catch (Exception e) {
            LOGGER.error("Error creating Ad object: {}", e.getMessage());
            return null;
        }
    }
    
    private static String extractHref(Element element) {
        Element urlElement = element.getElementsByAttributeValue("itemprop", "url").first();
        if (urlElement != null) {
            return urlElement.attr("href");
        }
        
        // Fallback: поиск ссылки в других местах
        Element linkElement = element.select("a[href]").first();
        if (linkElement != null) {
            return linkElement.attr("href");
        }
        
        return null;
    }
    
    private static String extractPrice(Element element) {
        Element priceElement = element.getElementsByAttributeValue("itemprop", "price").first();
        if (priceElement != null) {
            return priceElement.attr("content");
        }
        
        // Fallback: поиск цены в тексте
        Element priceTextElement = element.select("[data-marker=item-price], .price").first();
        if (priceTextElement != null) {
            return priceTextElement.text();
        }
        
        return "";
    }
    
    private static String extractDate(Element element) {
        Element dateElement = element.getElementsByAttributeValue("data-marker", "item-date").first();
        if (dateElement != null) {
            return dateElement.text();
        }
        
        // Fallback: поиск даты в других местах
        Element dateTextElement = element.select("[data-marker*=date], .date").first();
        if (dateTextElement != null) {
            return dateTextElement.text();
        }
        
        return "";
    }
    
    private static String extractDescription(Element element) {
        Element descElement = element.select("meta[itemprop=description]").first();
        if (descElement != null) {
            return descElement.attr("content");
        }
        
        // Fallback: поиск описания в других местах
        Element descTextElement = element.select("[data-marker=item-title], .title, h3").first();
        if (descTextElement != null) {
            return descTextElement.text();
        }
        
        return "";
    }


    public static HashMap<String, Ad> parseToMap(Document page) {
        if (page == null) {
            LOGGER.error("Document is null, cannot parse");
            return new HashMap<>();
        }
        
        Element catalogElement = findCatalogElement(page);
        if (catalogElement == null) {
            LOGGER.error("Catalog element not found, unable to parse ads");
            return new HashMap<>();
        }
        
        Elements adElements = catalogElement.getElementsByAttributeValue("data-marker", "item");
        if (adElements.isEmpty()) {
            LOGGER.warn("No ad elements found in catalog");
            return new HashMap<>();
        }
        
        HashMap<String, Ad> entityHashMap = new HashMap<>();
        int skippedCount = 0;
        int errorCount = 0;
        
        for (Element element : adElements) {
            try {
                if (isPromotedAd(element)) {
                    skippedCount++;
                    continue;
                }

                Ad ad = parseAdElement(element);
                if (ad != null) {
                    entityHashMap.put(String.valueOf(ad.getId()), ad);
                } else {
                    errorCount++;
                }
                
            } catch (Exception e) {
                LOGGER.error("Error parsing ad element: {}", e.getMessage(), e);
                errorCount++;
            }
        }
        
        LOGGER.info("Parsing to map completed: {} ads parsed, {} promoted ads skipped, {} errors", 
                   entityHashMap.size(), skippedCount, errorCount);
        return entityHashMap;
    }

}
