package client.utils;

import commons.Participant;

public record Transfer(Participant sender, int amount, Participant receiver) {}
