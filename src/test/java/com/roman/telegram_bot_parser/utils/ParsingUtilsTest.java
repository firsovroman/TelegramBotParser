package com.roman.telegram_bot_parser.utils;

import com.roman.telegram_bot_parser.dao.Ad;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParsingUtilsTest {

    @Test
    void testParseToList_ValidHtml() {
        String sampleHtml = "<div class=\"items-items-kAJAg\" data-marker=\"catalog-serp\">" +
                "    <div data-marker=\"item\" data-item-id=\"1\">" +
                "        <a href=\"/item/1\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"100\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-01</span>" +
                "        <meta itemprop=\"description\" content=\"Test description 1\">" +
                "    </div>" +
                "    <div data-marker=\"item\" data-item-id=\"2\">" +
                "        <a href=\"/item/2\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"200\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-02</span>" +
                "        <meta itemprop=\"description\" content=\"Test description 2\">" +
                "    </div>" +
                "</div>";
        Document testDocument = Jsoup.parse(sampleHtml);

        List<Ad> adList = ParsingUtils.parseToList(testDocument);

        assertEquals(2, adList.size());
        
        Ad ad1 = adList.get(0);
        assertEquals(1, ad1.getId());
        assertEquals("https://www.avito.ru/item/1", ad1.getLink());
        assertEquals("100", ad1.getPrice());
        assertEquals("2023-07-01", ad1.getDate());
        assertEquals("Test description 1", ad1.getDescription());

        Ad ad2 = adList.get(1);
        assertEquals(2, ad2.getId());
        assertEquals("https://www.avito.ru/item/2", ad2.getLink());
        assertEquals("200", ad2.getPrice());
        assertEquals("2023-07-02", ad2.getDate());
        assertEquals("Test description 2", ad2.getDescription());
    }

    @Test
    void testParseToList_NullDocument() {
        List<Ad> adList = ParsingUtils.parseToList(null);
        assertNotNull(adList);
        assertTrue(adList.isEmpty());
    }

    @Test
    void testParseToList_EmptyDocument() {
        Document emptyDocument = Jsoup.parse("<html></html>");
        List<Ad> adList = ParsingUtils.parseToList(emptyDocument);
        assertNotNull(adList);
        assertTrue(adList.isEmpty());
    }

    @Test
    void testParseToList_NoCatalogElement() {
        String htmlWithoutCatalog = "<div>Some other content</div>";
        Document testDocument = Jsoup.parse(htmlWithoutCatalog);
        
        List<Ad> adList = ParsingUtils.parseToList(testDocument);
        assertNotNull(adList);
        assertTrue(adList.isEmpty());
    }

    @Test
    void testParseToList_FallbackSelector() {
        String htmlWithFallbackSelector = "<div data-marker=\"catalog-serp\">" +
                "    <div data-marker=\"item\" data-item-id=\"1\">" +
                "        <a href=\"/item/1\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"100\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-01</span>" +
                "    </div>" +
                "</div>";
        Document testDocument = Jsoup.parse(htmlWithFallbackSelector);

        List<Ad> adList = ParsingUtils.parseToList(testDocument);
        assertEquals(1, adList.size());
    }

    @Test
    void testParseToList_PromotedAdSkipped() {
        String htmlWithPromotedAd = "<div class=\"items-items-kAJAg\" data-marker=\"catalog-serp\">" +
                "    <div data-marker=\"item\" data-item-id=\"1\" class=\"styles-redesign-YLctS\">" +
                "        <a href=\"/item/1\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"100\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-01</span>" +
                "    </div>" +
                "    <div data-marker=\"item\" data-item-id=\"2\">" +
                "        <a href=\"/item/2\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"200\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-02</span>" +
                "    </div>" +
                "</div>";
        Document testDocument = Jsoup.parse(htmlWithPromotedAd);

        List<Ad> adList = ParsingUtils.parseToList(testDocument);
        assertEquals(1, adList.size()); // Only non-promoted ad should be included
        assertEquals(2, adList.get(0).getId());
    }

    @Test
    void testParseToList_MissingAdId() {
        String htmlWithMissingId = "<div class=\"items-items-kAJAg\" data-marker=\"catalog-serp\">" +
                "    <div data-marker=\"item\">" + // Missing data-item-id
                "        <a href=\"/item/1\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"100\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-01</span>" +
                "    </div>" +
                "    <div data-marker=\"item\" data-item-id=\"2\">" +
                "        <a href=\"/item/2\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"200\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-02</span>" +
                "    </div>" +
                "</div>";
        Document testDocument = Jsoup.parse(htmlWithMissingId);

        List<Ad> adList = ParsingUtils.parseToList(testDocument);
        assertEquals(1, adList.size()); // Only valid ad should be included
        assertEquals(2, adList.get(0).getId());
    }

    @Test
    void testParseToList_MissingUrl() {
        String htmlWithMissingUrl = "<div class=\"items-items-kAJAg\" data-marker=\"catalog-serp\">" +
                "    <div data-marker=\"item\" data-item-id=\"1\">" +
                "        <span itemprop=\"price\" content=\"100\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-01</span>" +
                "    </div>" +
                "</div>";
        Document testDocument = Jsoup.parse(htmlWithMissingUrl);

        List<Ad> adList = ParsingUtils.parseToList(testDocument);
        assertTrue(adList.isEmpty()); // Ad without URL should be filtered out
    }

    @Test
    void testParseToMap_ValidHtml() {
        String sampleHtml = "<div class=\"items-items-kAJAg\" data-marker=\"catalog-serp\">" +
                "    <div data-marker=\"item\" data-item-id=\"1\">" +
                "        <a href=\"/item/1\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"100\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-01</span>" +
                "    </div>" +
                "    <div data-marker=\"item\" data-item-id=\"2\">" +
                "        <a href=\"/item/2\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"200\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-02</span>" +
                "    </div>" +
                "</div>";
        Document testDocument = Jsoup.parse(sampleHtml);

        HashMap<String, Ad> entityHashMap = ParsingUtils.parseToMap(testDocument);

        assertEquals(2, entityHashMap.size());

        Ad ad1 = entityHashMap.get("1");
        assertNotNull(ad1);
        assertEquals("https://www.avito.ru/item/1", ad1.getLink());
        assertEquals("100", ad1.getPrice());
        assertEquals("2023-07-01", ad1.getDate());

        Ad ad2 = entityHashMap.get("2");
        assertNotNull(ad2);
        assertEquals("https://www.avito.ru/item/2", ad2.getLink());
        assertEquals("200", ad2.getPrice());
        assertEquals("2023-07-02", ad2.getDate());
    }

    @Test
    void testParseToMap_NullDocument() {
        HashMap<String, Ad> entityHashMap = ParsingUtils.parseToMap(null);
        assertNotNull(entityHashMap);
        assertTrue(entityHashMap.isEmpty());
    }

    @Test
    void testParseToMap_EmptyDocument() {
        Document emptyDocument = Jsoup.parse("<html></html>");
        HashMap<String, Ad> entityHashMap = ParsingUtils.parseToMap(emptyDocument);
        assertNotNull(entityHashMap);
        assertTrue(entityHashMap.isEmpty());
    }
}
