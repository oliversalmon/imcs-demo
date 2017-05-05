package com.example.mu.domain;

import com.google.gson.Gson;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;

/**
 * Created by oliverbuckley-salmon on 28/04/2017.
 */
public class Party implements Portable{

    private String partyId
    ,               shortName
    ,               name
    ,               role
    ,               positionAccountId;
    public static final int FACTORY_ID = 1
    ,                       CLASS_ID = 3;

    public Party() {
    }

    public Party(String partyId, String shortName, String name, String role, String positionAccountId) {
        this.partyId = partyId;
        this.shortName = shortName;
        this.name = name;
        this.role = role;
        this.positionAccountId = positionAccountId;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getFactoryId() {
        return FACTORY_ID;
    }

    public int getClassId() {
        return CLASS_ID;
    }

    public String getPositionAccountId() {
        return positionAccountId;
    }

    public void setPositionAccountId(String positionAccountId) {
        this.positionAccountId = positionAccountId;
    }



    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("partyId", partyId);
        out.writeUTF("shortName", shortName);
        out.writeUTF("name", name);
        out.writeUTF("role", role);
        out.writeUTF("positionAccountId", positionAccountId);
    }

    @Override
    public void readPortable(PortableReader in) throws IOException {
        this.partyId = in.readUTF("partyId");
        this.shortName = in.readUTF("shortName");
        this.name = in.readUTF("name");
        this.role = in.readUTF("role");
        this.positionAccountId = in.readUTF("positionAccountId");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Party party = (Party) o;

        if (!partyId.equals(party.partyId)) return false;
        if (!shortName.equals(party.shortName)) return false;
        if (name != null ? !name.equals(party.name) : party.name != null) return false;
        if (role != null ? !role.equals(party.role) : party.role != null) return false;
        return positionAccountId.equals(party.positionAccountId);
    }

    @Override
    public int hashCode() {
        int result = partyId.hashCode();
        result = 31 * result + shortName.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + positionAccountId.hashCode();
        return result;
    }
}
