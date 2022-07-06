package engineer.pol.client.renderer;

import engineer.pol.client.CinematicExtension;

public class PlayerRendererManager implements CinematicExtension {

    private boolean renderFirstPersonHand;
    /*private boolean renderModelFirstPerson;
    private boolean renderRightArm;
    private boolean renderLeftArm;
    private boolean renderRightLeg;
    private boolean renderLeftLeg;
    private boolean renderHead;*/

    public PlayerRendererManager() {
        renderFirstPersonHand = false;
        /*renderModelFirstPerson = true;
        renderRightArm = true;
        renderLeftArm = true;
        renderRightLeg = true;
        renderLeftLeg = true;
        renderHead = true;*/
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    public boolean isRenderFirstPersonHand() {
        return renderFirstPersonHand;
    }

    /*public boolean isRenderModelFirstPerson() {
        return renderModelFirstPerson;
    }

    public boolean isRenderRightArm() {
        return renderRightArm;
    }

    public boolean isRenderLeftArm() {
        return renderLeftArm;
    }

    public boolean isRenderRightLeg() {
        return renderRightLeg;
    }

    public boolean isRenderLeftLeg() {
        return renderLeftLeg;
    }

    public boolean isRenderHead() {
        return renderHead;
    }*/
}
