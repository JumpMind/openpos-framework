package org.jumpmind.pos.tax.service;

import org.jumpmind.pos.service.EndpointDispatcher;
import org.jumpmind.pos.tax.model.TaxCalculationRequest;
import org.jumpmind.pos.tax.model.TaxCalculationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tax")
public class TaxService {

    @Autowired
    private EndpointDispatcher endpointDispatcher;

    @RequestMapping("/calculateTax")
    public TaxCalculationResponse calculateTax(TaxCalculationRequest request) {
        return endpointDispatcher.dispatch("/calculateTax", request);
    }

}
