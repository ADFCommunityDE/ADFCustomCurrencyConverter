package de.teampb.conv;

import java.math.BigDecimal;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import oracle.adf.share.logging.ADFLogger;

/**
 * Custom JSF Faces Converter to convert entries in Currency Input Texts in a correct way. Oracle ADF seems to be a bit
 * confused, if the grouping character in a Locale is '.' and the decimal delimiter is '.' (for example in Germany).
 */
@FacesConverter("de.teampb.conv.CurrencyConverter")
public class CurrencyConverter implements Converter {

    /**
     * Class logger.
     */
    private static final ADFLogger LOG = ADFLogger.createADFLogger(CurrencyConverter.class.getName());

    /**
     * Converter method from UI Entry to data value. Takes an Input String from the UI Component and converts it to a
     * BigDecimal value for data changes.
     *
     * @param facesContext current JSF Context
     * @param uIComponent Component that has a new value
     * @param string Entered String value (may contain groupings, delimiter or currency symbol)
     * @return correctly converted BigDecimal object for the given input
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String string) {
        LOG.entering("CurrencyConverter", "getAsObject", new Object[] { uIComponent, string });
        BigDecimal result;

        final Locale locale = facesContext.getViewRoot().getLocale();
        LOG.finest("Locale for Conversion: " + locale.getLanguage());

        if (string != null && !string.isEmpty()) {
            LOG.finest("Parsing numeric sanity of string...");
            Pattern regex = Pattern.compile("[&:;=?@#|]|[a-zA-Z]");
            Matcher matcher = regex.matcher(string);
            if (matcher.find()) {
                NumberFormat f = NumberFormat.getCurrencyInstance(locale);
                throw new ConverterException(f.format(123456.78));
            }
            LOG.finest("...done");

            String res = string;
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

            // get Locale specific grouping and decimal seperators
            char sep = symbols.getDecimalSeparator();
            LOG.finest("Decimal Separator used: " + sep);
            char grp = symbols.getGroupingSeparator();
            LOG.finest("Grouping Separator used: " + grp);

            // remove groupings
            String valueConverted = string.replace(grp, Character.MIN_VALUE);
            LOG.finest("String removed of groups:" + valueConverted);

            // change decimal seperator to "US" standards
            valueConverted = valueConverted.replace(sep, ".".charAt(0));
            LOG.finest("String with changed decimal separator:" + valueConverted);

            // throw away any non numeric stuff
            res = valueConverted.replaceAll("[^\\d.]+", "");

            LOG.finest("Expected result:" + res);
            result = new BigDecimal(res);
        } else {
            LOG.finest("Input was empty, so create a zero object");
            // this is of course project specific, can also return null etc.
            result = new BigDecimal(0);
        }
        LOG.exiting("CurrencyConverter", "getAsObject", result);
        return result;
    }

    /**
     * Converter method to create a correct currency String for a given data object.
     *
     * @param facesContext current JSF Context
     * @param uIComponent UI Component that will get the String value
     * @param object data value that shall be converted
     * @return correct String representation of data to a set Locale
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object object) {
        LOG.entering("CurrencyConverter", "getAsString", object);

        final Locale locale = facesContext.getViewRoot().getLocale();

        LOG.finest("Locale for Conversion: " + locale.getLanguage());
        NumberFormat f = NumberFormat.getCurrencyInstance(locale);
        String res = f.format(object);

        LOG.exiting("CurrencyConverter", "getAsString", res);
        return res;
    }
}
