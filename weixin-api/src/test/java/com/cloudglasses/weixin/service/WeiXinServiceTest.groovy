package com.cloudglasses.weixin.service

import app.SpringApplicationLauncher
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
@ContextConfiguration(classes = SpringApplicationLauncher)
class WeiXinServiceTest extends Specification {
    @Autowired
    ApiService service

    def getToken() {

        println service.getUserList(null)
        expect:
        true
    }

    def xml() {

        expect:
        true
    }

    def message() {
        TextMessage mess = new TextMessage( )
        println MessageUtil.textMessageToXml(mess)
        expect:
        true
    }
}

