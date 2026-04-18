package info.kornhuber.jobsearch.multitenancy;

public class TenantResolutionException extends RuntimeException {

    public TenantResolutionException(String message) {
        super(message);
    }
}