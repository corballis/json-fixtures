package ie.corballis.fixtures.assertion;

import uk.co.datumedge.hamcrest.json.SameJSONAs;

import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public enum MatchingMode {
    MATCHES,
    MATCHES_EXACTLY,
    MATCHES_WITH_STRICT_ORDER,
    MATCHES_EXACTLY_WITH_STRICT_ORDER;

    public SameJSONAs<String> getJsonMatcher(String json) {
        SameJSONAs<? super String> sameJSONAs = sameJSONAs(json);
        if (this == MATCHES_WITH_STRICT_ORDER) {
            sameJSONAs = sameJSONAs.allowingExtraUnexpectedFields();
        } else if (this == MATCHES_EXACTLY) {
            sameJSONAs = sameJSONAs.allowingAnyArrayOrdering();
        } else if (this == MATCHES) {
            sameJSONAs = sameJSONAs(json).allowingAnyArrayOrdering().allowingExtraUnexpectedFields();
        }
        return (SameJSONAs<String>) sameJSONAs;
    }

}
