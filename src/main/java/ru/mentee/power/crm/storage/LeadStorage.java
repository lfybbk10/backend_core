package ru.mentee.power.crm.storage;

import ru.mentee.power.crm.domain.Lead;

public class LeadStorage {
    private Lead[] leads = new Lead[100];

    public boolean add(Lead lead) {
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                if(leads[i].equals(lead)){
                    return false;
                }
            }
            else {
                leads[i] = lead;
                return true;
            }
        }

        throw new IllegalStateException("Storage is full, cannot add more leads");
    }

    public Lead[] findAll() {
        Lead[] result = new Lead[size()];
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                result[i] = leads[i];
            }
        }

        return result;
    }

    public int size() {
        int count = 0;
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                count++;
            }
        }
        return count;
    }
}
