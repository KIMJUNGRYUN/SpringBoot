package mysite.expense.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class DateTimeUtil {
    
    // sql date 를 문자열 포맷으로 변환하는 스태틱 메서드
    public static String convertDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    //문자열날짜 => sql Date 날짜
    public static Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = sdf.parse(dateString);
        return new Date(date.getTime());
    }

}
