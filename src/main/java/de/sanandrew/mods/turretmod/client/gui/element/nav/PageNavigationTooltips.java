package de.sanandrew.mods.turretmod.client.gui.element.nav;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

public class PageNavigationTooltips
        extends ElementParent<Integer>
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_page_nav_ttips");

    private final String navId;
    private final JsonObject tooltipData;

    private PageNavigation navCache;

    public PageNavigationTooltips(String navId, JsonObject tooltipData) {
        super();

        this.navId = navId;
        this.tooltipData = tooltipData;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        PageNavigation nav = this.getNav(gui);
        JsonObject lblData = this.tooltipData.getAsJsonObject("label");

        JsonUtils.addJsonProperty(this.tooltipData, "size", new int[] {nav.getTabWidth(), nav.getTabHeight()});

        for( int i = 0, max = nav.maxTabsShown; i < max; i++ ) {
            GuiElementInst txt;
            if( lblData != null ) {
                txt = new GuiElementInst(Text.Builder.fromJson(gui, lblData));
            } else {
                txt = new GuiElementInst(new Text.Builder(StringTextComponent.EMPTY).color(0xFFFFFF).shadow(true).get(gui));
            }
            final int fi = i;
            txt.get(Text.class).setTextFunc((g, o) -> MiscUtils.apply(getTab(nav, fi), e -> new TranslationTextComponent(Lang.TCU_TEXT.get(e.getValue())), StringTextComponent.EMPTY));

            Tooltip ttip = Tooltip.Builder.buildFromJson(gui, this.tooltipData, b -> (g, o) -> txt).get(gui);
            this.put(i, new GuiElementInst(new int[] {nav.getTabX(i), 0}, ttip).initialize(gui));
        }

        super.setup(gui, inst);

        this.updateVisibilities(gui);
    }

    private static Map.Entry<GuiElementInst, ResourceLocation> getTab(PageNavigation nav, int index) {
        int i = 0;
        for( Map.Entry<GuiElementInst, ResourceLocation> e : nav.shownTabs.entrySet() ) {
            if( i++ == index ) {
                return e;
            }
        }

        return null;
    }

    private PageNavigation getNav(IGui gui) {
        if( this.navCache == null ) {
            this.navCache = gui.getDefinition().getElementById(this.navId).get(PageNavigation.class);
        }

        return this.navCache;
    }

    @Override
    public void tick(IGui gui, GuiElementInst e) {
        this.updateVisibilities(gui);

        super.tick(gui, e);
    }

    private void updateVisibilities(IGui gui) {
        for( Map.Entry<Integer, GuiElementInst> e : this.namedChildren.entrySet() ) {
            e.getValue().setVisible(getTab(this.getNav(gui), e.getKey()) != null);
        }
    }

    public static class Builder
            implements IBuilder<PageNavigationTooltips>
    {
        private final String navId;
        private final JsonObject ttipData;

        public Builder(String navId, JsonObject ttipData) {
            this.navId = navId;
            this.ttipData = MiscUtils.get(ttipData, JsonObject::new);
        }

        @Override
        public void sanitize(IGui iGui) { /* no-op */ }

        @Override
        public PageNavigationTooltips get(IGui gui) {
            return new PageNavigationTooltips(this.navId, this.ttipData);
        }

        public static Builder buildFromJson(JsonObject data) {
            return new Builder(JsonUtils.getStringVal(data.get("for")), data.getAsJsonObject("tooltip"));
        }

        public static PageNavigationTooltips fromJson(IGui gui, JsonObject data) {
            return buildFromJson(data).get(gui);
        }
    }
}
