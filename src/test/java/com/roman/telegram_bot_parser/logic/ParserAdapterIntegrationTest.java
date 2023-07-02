package com.roman.telegram_bot_parser.logic;


import com.roman.telegram_bot_parser.dao.Ad;
import com.roman.telegram_bot_parser.dao.AdsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@SpringBootTest
@TestPropertySource("classpath:application.properties")
class ParserAdapterIntegrationTest {

    @Mock
    private AdsRepository adsRepository;
    @Autowired
    private ParserAdapter parserAdapter;


    @Test
    void testParseAndSaveAds() {
        parserAdapter.parseFilterAndSaveAds();
        verify(adsRepository).saveAll(any());
    }

    @Test
    void filterByTimeAndDescription_ShouldFilterAdsByTimeAndDescription() {

//        parserAdapter.addWordsToExcluded(Collections.singletonList(""));
        parserAdapter.addWordsToExcluded(Collections.singletonList("Unwanted"));

        // Arrange
        List<Ad> mockAds = new ArrayList<>();
        mockAds.add(new Ad(1, "http://example.com/1", "100", "10 минут", "Description 1"));
        mockAds.add(new Ad(2, "http://example.com/2", "200", "5 минут", "Description 2"));
        mockAds.add(new Ad(2, "http://example.com/2", "200", "1 день", "Description 2")); // возраст превышен
        mockAds.add(new Ad(3, "http://example.com/3", "300", "5 минут", "Unwanted description")); // недопустимое слово

        // Act
        List<Ad> filteredAds = parserAdapter.filterByTimeAndDescription(mockAds);

        // Assert
        // The unwanted ad with the description "Unwanted description" should be filtered out
        Assertions.assertEquals(2, filteredAds.size());
        Assertions.assertNotEquals("Unwanted description", filteredAds.get(0).getDescription());
        Assertions.assertNotEquals("Unwanted description", filteredAds.get(1).getDescription());
    }
}