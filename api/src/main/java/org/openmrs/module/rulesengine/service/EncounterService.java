package org.openmrs.module.rulesengine.service;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

import java.util.Arrays;
import java.util.List;

public class EncounterService {

    private final String REGISTRATION_ENCOUNTER_TYPE = "REG";

    public Encounter getLatestEncounterByPatient(Patient patient) {
        EncounterType registration = Context.getEncounterService().getEncounterType(REGISTRATION_ENCOUNTER_TYPE);
        List<Encounter> encounters = Context.getEncounterService()
            .getEncounters(patient, null, null, null, null, Arrays.asList(registration), null, null, null, false);

        Encounter selectedEncounter = encounters.get(0);

        for (Encounter encounter : encounters) {
            if (encounter.getEncounterDatetime().after(selectedEncounter.getEncounterDatetime())) {
                selectedEncounter = encounter;
            }
        }
        return selectedEncounter;
    }
}
