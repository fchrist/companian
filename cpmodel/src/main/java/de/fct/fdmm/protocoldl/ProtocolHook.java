package de.fct.fdmm.protocoldl;

import de.fct.fdmm.hotspot.HookCall;

public interface ProtocolHook {

    public HookProtocolState getTarget();

    public String getHookSignature();

    public HookCall getHookCall();
    
    public MessageType getMessageType();
    
    public String toString();
    
}
