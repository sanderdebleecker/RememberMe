package be.sanderdebleecker.herinneringsapp.Helpers.Forms;

import android.support.design.widget.TextInputEditText;


public class Validator {
    private final static int NAME_MIN_CHARS = 2;
    private final static int QUESTION_MIN_CHARS = 5;
    private final static int PASS_MIN_CHARS = 8;
    private final static int PASS_MAX_CHARS = 30;
    private final static int MAX_CHARS = 100;
    private final static int SPACE = 0x20;

    public static boolean validateNameFields(TextInputEditText... fields) {
        boolean result;
        boolean hasErrors=false;
        for(TextInputEditText field : fields) {
            result = validateName(field);
            if(!result) {
                hasErrors=true;
            }
        }
        return !hasErrors;
    }
    public static boolean validateRequiredFields(TextInputEditText... fields) {
        boolean result;
        boolean hasErrors=false;
        for(TextInputEditText field : fields) {
            result = validateRequiredField(field);
            if(!result) {
                hasErrors=true;
            }
        }
        return !hasErrors;
    }
    public static boolean validateQuestionFields(TextInputEditText... fields) {
        boolean result;
        boolean hasErrors=false;
        for(TextInputEditText field : fields) {
            result = validateQuestion(field);
            if(!result) {
                hasErrors=true;
            }
        }
        return !hasErrors;
    }

    //min 2 chars , only alphanumeric
    public static boolean validateRequiredField(TextInputEditText field) {
        String value = field.getText().toString().trim();
        boolean hasErrors = false;
        if(value.length()<1) {
            hasErrors = true;
            field.setError("veld is leeg");
        }
        return !hasErrors;
    }
    public static boolean validateName(TextInputEditText field){
        String value = field.getText().toString().trim();
        boolean hasErrors = false;
        //MIN CHARS
        if(value.length()< NAME_MIN_CHARS) {
            field.setError("min "+ NAME_MIN_CHARS +" karakters");
            hasErrors = true;
        }
        //MAX CHARS
        if(value.length()> MAX_CHARS) {
            field.setError("max "+ MAX_CHARS +" karakters");
            hasErrors = true;
        }
        if(!isOnlyLettersAndSpaces(value)){
            field.setError("mag enkel letters bevatten");
            hasErrors = true;
        }
        return !hasErrors;
    }
    //min 5 chars
    public static boolean validateQuestion(TextInputEditText field){
        String value = field.getText().toString().trim();
        boolean hasErrors = false;
        //MIN CHARS
        if(value.length()< QUESTION_MIN_CHARS) {
            field.setError("min "+ QUESTION_MIN_CHARS +" karakters");
            hasErrors = true;
        }
        //MAX CHARS
        if(value.length()> MAX_CHARS) {
            field.setError("max "+ MAX_CHARS +" karakters");
            hasErrors = true;
        }
        return !hasErrors;
    }
    public static boolean validatePass(TextInputEditText field){
        String value = field.getText().toString().trim();
        boolean hasErrors = false;
        //MIN CHARS
        if(value.length() < PASS_MIN_CHARS) {
            field.setError("min "+ PASS_MIN_CHARS +" karakters");
            hasErrors = true;
        }
        //MAX CHARS
        if(value.length() > PASS_MAX_CHARS) {
            field.setError("max "+ MAX_CHARS +" karakters");
            hasErrors = true;
        }
        //Letters + Number
        if( !(hasLetters(value) && hasNumbers(value)) ) {
            field.setError("letters en cijfers");
            hasErrors = true;
        }
        return !hasErrors;
    }
    private static boolean isOnlyLetters(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (c <= 0x40 || (c > 0x5a && c <= 0x60) || c > 0x7a)
                return false;
        }
        return true;
    }
    private static boolean isOnlyLettersAndSpaces(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if ( (c <= 0x40 && c!=SPACE) || (c > 0x5a && c <= 0x60) || c > 0x7a)
                return false;
        }
        return true;
    }
    private static boolean hasLetters(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if ((c > 0x40 && c < 0x5B) || (c > 0x60 && c < 0x7B) || (c > 0x7F && c < 0x9B))
                return true;
        }
        return false;
    }
    private static boolean hasNumbers(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (c > 0x2F && c < 0x3A )
                return true;
        }
        return false;
    }
    //helper func's
    public static String getValue(TextInputEditText field) {
        return field.getText().toString().trim();
    }
}
