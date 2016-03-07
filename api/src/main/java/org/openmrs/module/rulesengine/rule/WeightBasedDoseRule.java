package org.openmrs.module.rulesengine.rule;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.*;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.rulesengine.CIELDictionary;
import org.openmrs.module.rulesengine.contract.Dose;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class WeightBasedDoseRule implements DoseRule {

    private final String REGISTRATION_ENCOUNTER_TYPE = "REG";

    @Override
    public Dose calculateDose(String patientUuid, Double baseDose) throws Exception {
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        Encounter selectedEncounter = getLatestEncounterByPatient(patient);

        Double weight = getWeight(patient,selectedEncounter);

        double roundedUpDoseValue = new BigDecimal(weight * baseDose).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return new Dose(roundedUpDoseValue, Dose.DoseUnit.mg);
    }

    private Encounter getLatestEncounterByPatient(Patient patient) {
        EncounterType registration = Context.getEncounterService().getEncounterType(REGISTRATION_ENCOUNTER_TYPE);
        List<Encounter> encounters = Context.getEncounterService()
            .getEncounters(patient, null, null, null, null, Arrays.asList(registration), null, null, null, false);

        Encounter selectedEncounter = encounters.get(0);

        for (Encounter encounter : encounters) {
            if(encounter.getEncounterDatetime().after(selectedEncounter.getEncounterDatetime())){
                selectedEncounter = encounter;
            }
        }
        return selectedEncounter;
    }

    private Double getWeight(Person person, Encounter selectedEncounter) throws Exception {
        ObsService obsService = Context.getObsService();
        Concept weight = Context.getConceptService().getConceptByUuid(CIELDictionary.WEIGHT_UUID);

        List<Obs> obss = obsService.getObservations(Arrays.asList(person), Arrays.asList(selectedEncounter), Arrays.asList(weight),
            null, null, null, null, null, null, null, null, false);
        if(CollectionUtils.isEmpty(obss)){
            throw new Exception("Weight is not available");
        }
        return obss.get(0).getValueNumeric();
    }

}
