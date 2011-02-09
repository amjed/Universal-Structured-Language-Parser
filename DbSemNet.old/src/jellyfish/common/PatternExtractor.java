/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jellyfish.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author QB
 */
public class PatternExtractor {

    private String regex;
    private Pattern pattern;

    public PatternExtractor(String regex) {
        this(regex,Pattern.CASE_INSENSITIVE);
    }

    public PatternExtractor(String regex, int flags) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex, flags);
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getRegex() {
        return regex;
    }

    
    public void extractMatchingGroups(String input, List<String> output)
    {
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            for (int i=1; i<=matcher.groupCount(); ++i) {
                String group = matcher.group(i);
                if (group!=null) {
                    output.add(group);
                }
            }
        }
    }

    public boolean matches(String input)
    {
        return pattern.matcher(input).matches();
    }

    @Override
    public String toString() {
        return regex;
    }
    

}
