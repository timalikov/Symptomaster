package com.symptomaster.ws.models;

import com.google.gson.annotations.Expose;

import java.util.*;

public class Transaction {
    @Expose
    private String transactionId;
    @Expose
    List<TransactionEntry> transactionEntryList;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<TransactionEntry> getTransactionEntryList() {
        return transactionEntryList;
    }

    public void setTransactionEntryList(List<TransactionEntry> transactionEntryList) {
        this.transactionEntryList = transactionEntryList;
    }

    public static class Mapper {
        public static List<Transaction> map(List<TransactionEntry> transactionEntryList) {
            List<Transaction> transactionList = new ArrayList<>();

            Map<String, List<TransactionEntry>> map = new LinkedHashMap<>();

            for (TransactionEntry transactionEntry : transactionEntryList) {
                String transactionId = transactionEntry.getTransactionId();

                List<TransactionEntry> transactionEntryListLocal = new ArrayList<>();

                if(map.containsKey(transactionId)) {
                    transactionEntryListLocal = map.get(transactionId);
                }

                transactionEntryListLocal.add(transactionEntry);
                map.put(transactionId, transactionEntryListLocal);
            }

            for(String key : map.keySet()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(key);
                transaction.setTransactionEntryList(map.get(key));
                transactionList.add(transaction);
            }

            return transactionList;
        }
    }
}
