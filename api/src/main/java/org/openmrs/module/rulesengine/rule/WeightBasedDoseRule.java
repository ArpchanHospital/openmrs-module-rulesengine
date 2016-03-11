package org.openmrs.module.rulesengine.rule;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.rulesengine.domain.Dose;
import org.openmrs.module.rulesengine.service.EncounterService;
import org.openmrs.module.rulesengine.service.ObservationService;

import java.math.BigDecimal;

public class WeightBasedDoseRule {

    private final ObservationService observationService = new ObservationService();
    private final EncounterService encounterService = new EncounterService();

    public Dose calculateDose(String patientUuid, Double baseDose) throws Exception {
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        Encounter selectedEncounter = encounterService.getLatestEncounterByPatient(patient);

        Double weight = observationService.getLatestWeight(patient, selectedEncounter);

        double roundedUpDoseValue = new BigDecimal(weight * baseDose).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return new Dose(roundedUpDoseValue, Dose.DoseUnit.mg);
    }

}
