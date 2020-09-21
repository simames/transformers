package com.aequilibrium.transformer.service.logic;

import com.aequilibrium.transformer.api.model.Transformer;
import com.aequilibrium.transformer.api.model.TransformerEnumType;
import com.aequilibrium.transformer.api.model.TransformerWinnerNames;
import com.aequilibrium.transformer.persistence.service.ITransformerRepository;
import com.aequilibrium.transformer.service.converter.TransformerConverter;
import com.aequilibrium.transformer.service.model.BattleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;


public class TransformerBattle {

    private final ITransformerRepository iTransformerRepository;


    private final TransformerConverter converter;

    private List<Transformer> allTransformers;
    private List<Transformer> descepticons = new ArrayList<>();
    private List<Transformer> autobots = new ArrayList<>();
    private List<Transformer> autobotsWinningTeam = new ArrayList<>();
    private List<Transformer> descepticonWinningtTeam = new ArrayList<>();
    private List<Transformer> survivingMembersOfLosingTeam = new ArrayList<>();
    private Integer numberOfBattles;
    private Transformer transformerWinner;

    public TransformerBattle(List<Transformer> transformers, ITransformerRepository iTransformerRepository,TransformerConverter converter) {
        this.converter = converter;
        this.iTransformerRepository = iTransformerRepository;
        this.allTransformers = transformers;
        for (Transformer transformer : transformers) {
            if (TransformerEnumType.DESEPTICAN.getCode().equals(transformer.getType())) {
                descepticons.add(transformer);
            } else if (TransformerEnumType.AUTOBOT.getCode().equals(transformer.getType())) {
                autobots.add(transformer);
            }
        }
    }

    public BattleResult battle() {
        Collections.sort(descepticons);
        Collections.sort(autobots);
        numberOfBattles = 0;
        while (descepticons.size() > 0 && autobots.size() > 0) {
            if (twoTransformerBattle(descepticons.get(0), autobots.get(0))) {
                numberOfBattles++;
            }
        }
        return createBattleResult();
    }

    private BattleResult createBattleResult() {
        BattleResult battleResult = new BattleResult();
        battleResult.setBattleNumbers(numberOfBattles);
        battleResult.setWinningTeam(getTransformersWinningTeam());
        battleResult.setSurvivingMembersOfTheLosingTeam(getSurvivingMembersOfLosingTeam());
        battleResult.setWinner(this.transformerWinner);
        return battleResult;
    }


    private List<Transformer> getTransformersWinningTeam() {
        if (descepticonWinningtTeam.size() > autobotsWinningTeam.size()) {
            setWinner(descepticonWinningtTeam);
            return descepticonWinningtTeam;
        } else {
            setWinner(autobotsWinningTeam);
            return autobotsWinningTeam;
        }
    }

    private void setWinner(List<Transformer> winningTeam) {
        winningTeam.sort(this::compareWinning);
        if (winningTeam.size() > 0) {
            this.transformerWinner = winningTeam.get(0);
        }
    }

    private int compareWinning(Transformer o1, Transformer o2) {
        return o1.getRanking() > o2.getRanking() ? 0 : -1;
    }


    public List<Transformer> getSurvivingMembersOfLosingTeam() {
        if (descepticonWinningtTeam.size() > autobotsWinningTeam.size()) {
            autobotsWinningTeam.addAll(autobots);
            return autobotsWinningTeam;
        } else {
            descepticonWinningtTeam.addAll(descepticons);
            return descepticonWinningtTeam;
        }
    }


    private boolean twoTransformerBattle(Transformer descepticon, Transformer autobot) {
        boolean isBattle = true;
        if (TransformerWinnerNames.OPTIMUS_PRIME.equals(descepticon.getName()) ||
                TransformerWinnerNames.PREDAKING.equals(descepticon.getName())) {
            isBattle = isBattleWinnerNames(autobot, descepticon, isBattle, descepticonWinningtTeam);
        } else if (TransformerWinnerNames.OPTIMUS_PRIME.equals(autobot.getName()) ||
                TransformerWinnerNames.PREDAKING.equals(autobot.getName())) {
            isBattle = isBattleWinnerNames(descepticon, autobot, isBattle, autobotsWinningTeam);
        } else if (abs(autobot.getCourage() - descepticon.getCourage()) > 3 &&
                abs(autobot.getStrength() - descepticon.getStrength()) > 2) {
            if (autobot.getCourage() > descepticon.getCourage()) {
                autobotsWinningTeam.add(autobot);
                iTransformerRepository.delete(converter.toEntity(descepticon));
            } else {
                descepticonWinningtTeam.add(descepticon);
                iTransformerRepository.delete(converter.toEntity(autobot));
            }
        } else if (abs(autobot.getSkill() - descepticon.getSkill()) > 2)
        {
            if (autobot.getSkill() > descepticon.getSkill()) {
                autobotsWinningTeam.add(autobot);
                iTransformerRepository.delete(converter.toEntity(descepticon));
            } else {
                descepticonWinningtTeam.add(descepticon);
                iTransformerRepository.delete(converter.toEntity(autobot));
            }
        } else {
            if (autobot.getRanking() > descepticon.getRanking()) {
                autobotsWinningTeam.add(autobot);
                iTransformerRepository.delete(converter.toEntity(descepticon));
            } else if (descepticon.getRanking() > autobot.getRanking()) {
                descepticonWinningtTeam.add(descepticon);
                iTransformerRepository.delete(converter.toEntity(autobot));
            } else {
                isBattle = false;
            }
        }
        descepticons.remove(descepticon);
        autobots.remove(autobot);
        return isBattle;
    }

    private boolean isBattleWinnerNames(Transformer descepticon, Transformer autobot, boolean isBattle, List<Transformer> autobotsWinningTeam) {
        if (!TransformerWinnerNames.OPTIMUS_PRIME.equals(descepticon.getName()) &&
                !TransformerWinnerNames.PREDAKING.equals(descepticon.getName())) {
            autobotsWinningTeam.add(autobot);
        } else {
            isBattle = false;
            descepticonWinningtTeam.clear();
            autobotsWinningTeam.clear();
            descepticons.clear();
            autobots.clear();
            iTransformerRepository.delete(converter.toEntity(descepticon));
            iTransformerRepository.delete(converter.toEntity(autobot));
            iTransformerRepository.deleteAll(converter.toEntities(allTransformers));
        }
        return isBattle;
    }


}
