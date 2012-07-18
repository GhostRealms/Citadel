package com.untamedears.citadel.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import com.lennardf1989.bukkitex.MyDatabase;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.citadel.entity.FactionMember;
import com.untamedears.citadel.entity.Member;
import com.untamedears.citadel.entity.Moderator;
import com.untamedears.citadel.entity.PersonalGroup;
import com.untamedears.citadel.entity.Reinforcement;
import com.untamedears.citadel.entity.ReinforcementKey;

/**
 * Created by IntelliJ IDEA.
 * User: chrisrico
 * Date: 3/18/12
 * Time: 3:58 PM
 * 
 * Last modified by JonnyD
 * 7/18/12
 */
public class CitadelDao extends MyDatabase {

    public CitadelDao(JavaPlugin plugin) {
        super(plugin);

        Configuration config = plugin.getConfig();

        initializeDatabase(
                config.getString("database.driver"),
                config.getString("database.url"),
                config.getString("database.username"),
                config.getString("database.password"),
                config.getString("database.isolation"),
                config.getBoolean("database.logging", false),
                config.getBoolean("database.rebuild", false)
        );

        config.set("database.rebuild", false);
        plugin.saveConfig();
    }

    @Override
    protected List<Class<?>> getDatabaseClasses() {
        return Arrays.asList(Faction.class, Member.class, FactionMember.class, Reinforcement.class, ReinforcementKey.class, PersonalGroup.class, Moderator.class);
    }

    public void save(Object object) {
        getDatabase().save(object);
    }

    public void delete(Object object) {
        getDatabase().delete(object);
    }

    public Faction getOrCreateFaction(String name, String founder) {
        Faction faction = findGroupByName(name);
        if (faction == null) {
            faction = new Faction(name, founder);
            save(faction);
        }
        return faction;
    }
    
    public Set<Faction> findGroupsByFounder(String founder){
    	return getDatabase().createQuery(Faction.class, "find faction where founder = :founder")
    			.setParameter("founder", founder)
    			.findSet();
    }
    
    public Set<FactionMember> findGroupsByMember(String memberName){
    	return getDatabase().createQuery(FactionMember.class, "find factionMember where memberName = :memberName")
    			.setParameter("memberName", memberName)
    			.findSet();
    }
    
    public Set<Moderator> findGroupsByModerator(String memberName){
    	return getDatabase().createQuery(Moderator.class, "find moderator where memberName = :memberName")
    			.setParameter("memberName", memberName)
    			.findSet();
    }

	public Set<Faction> findAllGroups() {
		return getDatabase().createQuery(Faction.class, "find factionMember")
				.findSet();
	}
	
	public Set<FactionMember> findAllGroupMembers(){
		return getDatabase().createQuery(FactionMember.class, "find factionMember")
				.findSet();
	}
	
	public Faction findGroup(String groupName){
		return getDatabase().createQuery(Faction.class, "find faction where name = :groupName")
				.setParameter("groupName", groupName)
				.findUnique();
	}

    public Faction findGroupByName(String name) {
        return getDatabase().createQuery(Faction.class, "find faction where name = :name")
                .setParameter("name", name)
                .findUnique();
    }
    
    public boolean hasGroupMember(String groupName, String memberName) {
        return getDatabase().createQuery(FactionMember.class, "find factionMember where faction_name = :groupName" +
        		" and member_name = :memberName")
        		.setParameter("groupName", groupName)
                .setParameter("memberName", memberName)
                .findRowCount() > 0;
    }

    public FactionMember findGroupMember(String groupName, String memberName) {
        return getDatabase().createQuery(FactionMember.class, "find factionMember where memberName = :memberName")
                .setParameter("memberName", memberName)
                .findUnique();
    }
    
    public Set<FactionMember> findMembersOfGroup(String groupName) {
        return getDatabase().createQuery(FactionMember.class, "find factionMember where factionName = :groupName")
        		.setParameter("groupName", groupName)
                .findSet();
    }
    
    public Set<Reinforcement> findReinforcementsByGroup(String groupName){
    	return getDatabase().createQuery(Reinforcement.class, "find reinforcement where name = :groupName")
    			.setParameter("groupName", groupName)
    			.findSet();    	
    }
    
    public List<Reinforcement> findAllReinforcements(){
    	return getDatabase().createQuery(Reinforcement.class, "find reinforcement")
    			.findList();
    }

    public Reinforcement findReinforcement(Block block) {
        return findReinforcement(block.getLocation());
    }

    public Reinforcement findReinforcement(Location location) {
        return getDatabase().createQuery(Reinforcement.class, "find reinforcement where x = :x and y = :y and z = :z and world = :world")
                .setParameter("x", location.getX())
                .setParameter("y", location.getY())
                .setParameter("z", location.getZ())
                .setParameter("world", location.getWorld().getName())
                .findUnique();
    }
    
    public void moveReinforcements(String from, String target){
    	SqlUpdate update = getDatabase().createSqlUpdate("UPDATE reinforcement SET name = :target, security_level = 1" +
    			" WHERE name = :from")
    			.setParameter("target", target)
    			.setParameter("from", from);
    	getDatabase().execute(update);
    }
    
    public int countReinforcements(){
    	SqlRow row = getDatabase().createSqlQuery("select count(*) as count from reinforcement").findUnique();
    	return row.getInteger("count");  
    }
    
    public int countGroups(){
    	SqlRow row = getDatabase().createSqlQuery("select count(*) as count from faction").findUnique();
    	return row.getInteger("count");  
    }

	public int countPlayerGroups(String playerName) {
    	SqlRow row = getDatabase().createSqlQuery("select count(*) as count from faction where founder = :founder")
    			.setParameter("founder", playerName)
    			.findUnique();
    	return row.getInteger("count"); 
	}

	public Set<Member> findAllMembers() {
		return getDatabase().createQuery(Member.class, "find member")
    			.findSet();
	}
	
	public Member findMember(String memberName){
		return getDatabase().createQuery(Member.class, "find member where member_name = :memberName")
				.setParameter("memberName", memberName)
				.findUnique();
	}

	public Set<PersonalGroup> findAllPersonalGroups() {
		return getDatabase().createQuery(PersonalGroup.class, "find personalGroup")
				.findSet();
	}

	public void addRein(int x, int y, int z, String groupName) {
		SqlUpdate update = getDatabase().createSqlUpdate("INSERT INTO reinforcement (x, y, z, world, material_id, durability, security_level, name)" +
				"Values (:x, :y, :z, 'World One', 1, 1800, 1, :groupName)")
				.setParameter("x", x)
				.setParameter("y", y)
				.setParameter("z", z)
				.setParameter("groupName", groupName);
		getDatabase().execute(update);
	}

	public void addGroup(String groupName) {
		SqlUpdate update = getDatabase().createSqlUpdate("INSERT INTO faction (name, founder) VALUES (:groupName, 'Gu3rr1lla')")
				.setParameter("groupName", groupName);
		getDatabase().execute(update);		
	}

	public PersonalGroup findPersonalGroup(String ownerName) {
		return getDatabase().createQuery(PersonalGroup.class, "find personalGroup where owner_name = :ownerName")
				.setParameter("ownerName", ownerName)
				.findUnique();
	}
	
    public boolean hasGroupModerator(String groupName, String memberName) {
        return getDatabase().createQuery(Moderator.class, "find moderator where faction_name = :groupName" +
        		" and member_name = :memberName")
        		.setParameter("groupName", groupName)
                .setParameter("memberName", memberName)
                .findRowCount() > 0;
    }

	public Set<Moderator> findModeratorsOfGroup(String groupName) {
		return getDatabase().createQuery(Moderator.class, "find moderator where faction_name = :groupName")
				.setParameter("groupName", groupName)
				.findSet();
	}
	
	public void removeAllMembersFromGroup(String groupName){
		SqlUpdate update = getDatabase().createSqlUpdate("delete from faction_member where faction_name = :groupName")
				.setParameter("groupName", groupName);
		getDatabase().execute(update);
	}
	
	public void removeAllModeratorsFromGroup(String groupName){
		SqlUpdate update = getDatabase().createSqlUpdate("delete from moderator where faction_name = :groupName")
				.setParameter("groupName", groupName);
		getDatabase().execute(update);
	}
}