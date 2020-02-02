package com.cloudglasses.repository

import app.SpringApplicationLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = SpringApplicationLauncher.class)
class OptometryDetailsRepositoryTest extends Specification {
    @Autowired
    private OptometryDetailRepository detailsRepository


    def "用户查询测试"() {
        println detailsRepository
        expect:
        true
    }
}
