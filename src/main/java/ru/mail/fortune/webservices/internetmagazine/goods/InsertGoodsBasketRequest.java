//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.26 at 07:56:33 PM YEKT 
//


package ru.mail.fortune.webservices.internetmagazine.goods;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="insertGoods" type="{http://ru/mail/fortune/webservices/internetmagazine/goods}GoodsBasketXml"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "insertGoods"
})
@XmlRootElement(name = "InsertGoodsBasketRequest")
public class InsertGoodsBasketRequest {

    @XmlElement(required = true)
    protected GoodsBasketXml insertGoods;

    /**
     * Gets the value of the insertGoods property.
     * 
     * @return
     *     possible object is
     *     {@link GoodsBasketXml }
     *     
     */
    public GoodsBasketXml getInsertGoods() {
        return insertGoods;
    }

    /**
     * Sets the value of the insertGoods property.
     * 
     * @param value
     *     allowed object is
     *     {@link GoodsBasketXml }
     *     
     */
    public void setInsertGoods(GoodsBasketXml value) {
        this.insertGoods = value;
    }

}
