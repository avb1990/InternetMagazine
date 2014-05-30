package ru.mail.fortune.internetmagazine.goods;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Utils {
	public static XMLGregorianCalendar date2XMLGregorianCalendar(Date value) {
		if (value == null)
			return null;
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(value);
		XMLGregorianCalendar calend = null;
		try {
			calend = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		} catch (DatatypeConfigurationException e) {
			Logger.getLogger(Utils.class.getName()).severe(e.getMessage());
			return null;
		}
		Calendar gCal = Calendar.getInstance();
		gCal.setTime(value);
		calend.setDay(gCal.get(Calendar.DAY_OF_MONTH));
		calend.setMonth(gCal.get(Calendar.MONTH) + 1);
		calend.setYear(gCal.get(Calendar.YEAR));
		calend.setHour(gCal.get(Calendar.HOUR_OF_DAY));
		calend.setMinute(gCal.get(Calendar.MINUTE));
		calend.setSecond(gCal.get(Calendar.SECOND));
		return calend;
	}

	public static Date xmlGregorianCalendar2date(XMLGregorianCalendar value) {
		if (value == null)
			return null;
		Calendar gCal = Calendar.getInstance();
		XMLGregorianCalendar calend = value;

		gCal.set(Calendar.DAY_OF_MONTH, calend.getDay());
		gCal.set(Calendar.MONTH, calend.getMonth() - 1);
		gCal.set(Calendar.YEAR, calend.getYear());
		gCal.set(Calendar.HOUR_OF_DAY, calend.getHour());
		gCal.set(Calendar.MINUTE, calend.getMinute());
		gCal.set(Calendar.SECOND, calend.getSecond());
		Date date = new Date();
		date.setTime(gCal.getTimeInMillis());
		return date;
	}
}
