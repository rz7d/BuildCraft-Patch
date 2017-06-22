package buildcraft.core;

import buildcraft.api.power.IRedstoneEngineReceiver;
import buildcraft.core.lib.engines.TileEngineBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEngineWood extends TileEngineBase {

  private boolean hasSent = false;

  @Override
  public String getResourcePrefix() {
    return "buildcraftcore:textures/blocks/engineWood";
  }

  @Override
  public ResourceLocation getTrunkTexture(EnergyStage stage) {
    return super.getTrunkTexture(stage == EnergyStage.RED && progress < 0.5 ? EnergyStage.YELLOW : stage);
  }

  @Override
  public int minEnergyReceived() {
    return 0;
  }

  @Override
  public int maxEnergyReceived() {
    return 500;
  }

  @Override
  protected EnergyStage computeEnergyStage() {
    double energyLevel = getEnergyLevel();
    if (energyLevel < 0.33f) {
      return EnergyStage.BLUE;
    } else if (energyLevel < 0.66f) {
      return EnergyStage.GREEN;
    } else if (energyLevel < 0.75f) {
      return EnergyStage.YELLOW;
    } else {
      return EnergyStage.RED;
    }
  }

  @Override
  public float getPistonSpeed() {
    if (!worldObj.isRemote) {
      return Math.max(0.08f * getHeatLevel(), 0.01f);
    }

    switch (getEnergyStage()) {
      case GREEN:
        return 0.02F;
      case YELLOW:
        return 0.04F;
      case RED:
        return 0.08F;
      default:
        return 0.01F;
    }
  }

  @Override
  public void engineUpdate() {
    super.engineUpdate();
    if (isRedstonePowered && worldObj.getTotalWorldTime() % 16 == 0) {
      addEnergy(10);
    }
  }

  @Override
  public boolean isBurning() {
    return isRedstonePowered;
  }

  @Override
  public int getMaxEnergy() {
    return 1000;
  }

  @Override
  public int calculateCurrentOutput() {
    return 10;
  }

  @Override
  public int maxEnergyExtracted() {
    return 10;
  }

  @Override
  protected void sendPower() {
    if (progressPart == 2 && !hasSent) {
      hasSent = true;

      TileEntity tile = getTile(orientation);

      if (tile instanceof IRedstoneEngineReceiver && ((IRedstoneEngineReceiver) tile).canConnectRedstoneEngine(orientation.getOpposite())) {
        super.sendPower();
      } else {
        this.energy = 0;
      }
    } else if (progressPart != 2) {
      hasSent = false;
    }
  }
}