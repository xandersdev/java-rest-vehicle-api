---
name: The Motor Register
description: A considered vehicle register for garages and showrooms.
colors:
  ink: "#222824"
  ink-soft: "#4e554e"
  paper: "#f1ece2"
  paper-deep: "#e7dfd0"
  line: "#ccc1ac"
  oxblood: "#762d31"
  oxblood-dark: "#5f2328"
  cream: "#faf5e9"
  copper: "#c29a70"
  success: "#397558"
  danger: "#a83e3e"
typography:
  display:
    fontFamily: "Newsreader, Georgia, serif"
    fontSize: "clamp(43px, 5vw, 78px)"
    fontWeight: 400
    lineHeight: 0.95
    letterSpacing: "-0.025em"
  headline:
    fontFamily: "Newsreader, Georgia, serif"
    fontSize: "clamp(34px, 3.5vw, 50px)"
    fontWeight: 400
    lineHeight: 1
    letterSpacing: "-0.02em"
  title:
    fontFamily: "Newsreader, Georgia, serif"
    fontSize: "24px"
    fontWeight: 500
    lineHeight: 1.1
  body:
    fontFamily: "Barlow, sans-serif"
    fontSize: "16px"
    fontWeight: 400
  label:
    fontFamily: "Barlow, sans-serif"
    fontSize: "11px"
    fontWeight: 600
    letterSpacing: "0.11em"
rounded:
  control: "3px"
components:
  button-primary:
    backgroundColor: "{colors.oxblood}"
    textColor: "{colors.cream}"
    rounded: "{rounded.control}"
    height: "42px"
    padding: "0 16px"
  button-primary-hover:
    backgroundColor: "{colors.oxblood-dark}"
  button-quiet:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    rounded: "{rounded.control}"
    height: "42px"
    padding: "0 16px"
  button-danger:
    backgroundColor: "transparent"
    textColor: "{colors.danger}"
    rounded: "{rounded.control}"
    height: "42px"
    padding: "0 16px"
  input:
    backgroundColor: "#fffdf7"
    textColor: "{colors.ink}"
    rounded: "{rounded.control}"
    height: "42px"
    padding: "0 11px"
  dossier:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.cream}"
    padding: "25px 27px 30px"
---

# Design System: The Motor Register

## Overview

**Creative North Star: "The Coachbuilder's Archive"**

The register presents cars with the care of an automotive editorial while remaining a working inventory. Warm paper, oxblood, graphite ink, and restrained copper suggest leather and metal without imitating a literal ledger. A single automotive image sets the atmosphere; vehicle names and records carry the daily work.

The same visual system serves a personal collection and a dealership's stock. Editorial serif identifies the place and the machines; precise sans type keeps controls, labels, and data quick to scan. Density comes from aligned rows and a compact summary, with a selected vehicle promoted into a dark dossier.

**Key Characteristics:**

- Warm, tactile color with a dark graphite masthead and dossier.
- Large serif identity and vehicle names paired with small, orderly sans labels.
- A dense collection table beside a selected record on wide screens.
- Restrained copper accents and one photographic strip.

## Colors

Oxblood anchors actions; warm neutrals carry the work surface; copper marks select brand and structural details.

### Primary

- **Archive Oxblood** (`colors.oxblood`): Main save actions, edition label, and interactive text.
- **Deep Oxblood** (`colors.oxblood-dark`): Primary button hover state.

### Secondary

- **Burnished Copper** (`colors.copper`): Crest, masthead action, dossier rule, and visible keyboard focus.

### Neutral

- **Graphite Ink** (`colors.ink`): Body text, masthead, dossier, and strong table rule.
- **Soft Ink** (`colors.ink-soft`): Supporting dark text.
- **Warm Paper** (`colors.paper`): Main canvas.
- **Deep Paper** (`colors.paper-deep`): Quiet hover and tonal separation.
- **Archive Line** (`colors.line`): Dividers and control boundaries.
- **Ivory Cream** (`colors.cream`): Light text on dark surfaces.

### Functional

- **Success Green** (`colors.success`): Positive feedback.
- **Danger Red** (`colors.danger`): Destructive action and error language.

**The Accent Rule.** Use oxblood for action and copper for the archive's identity and focus; keep both selective so the vehicle data remains prominent.

## Typography

**Display Font:** Newsreader (Georgia fallback).  
**Body and Label Font:** Barlow (sans-serif fallback).

Newsreader gives the title, collection heading, vehicle names, and dossier a crafted editorial voice. Barlow keeps counts, registration numbers, form labels, and actions crisp. Both families are hosted with the app.

### Hierarchy

- **Display** (`typography.display`): Hero statement; balanced lines and tight leading.
- **Headline** (`typography.headline`): Collection section title.
- **Title** (`typography.title`): Vehicle names in the inventory; dossier titles scale larger to fit their panel.
- **Body** (`typography.body`): Base controls and prose; table and explanatory copy step down to 13–15px where the implementation calls for denser scanning.
- **Label** (`typography.label`): Uppercase table headers with tracked letters; related metadata and edition labels follow the same compact sans character.

**The Two Voices Rule.** Reserve Newsreader for identity and vehicle emphasis; use Barlow for operational text and data.

## Layout

The page sits inside a centered shell (maximum width 1512px). The masthead, hero, summary strip, collection, and footer share responsive side padding (`clamp(20px, 4vw, 64px)`). The working area has a two-column grid: the inventory has more room than the selected dossier (`minmax(0, 1.65fr) minmax(290px, .8fr)`, 26px gap). Records use aligned table rows with strong top and fine row rules.

At 1000px the grid narrows and the date column drops. At 760px the inventory becomes one column and the dossier opens as a fixed bottom panel over a backdrop. At 520px the table becomes stacked vehicle rows, the two-column form tightens its gaps, and the compact masthead and hero adjust for narrow widths. Preserve readable data and obvious actions at each width.

## Elevation & Depth

The ordinary page is mostly flat. Dark and light planes, fine borders, and the photographic hero establish depth. Shadows are reserved for the full-height editor panel and mobile dossier when they overlay the page.

### Shadow Vocabulary

- **Editor panel** (`-22px 0 60px rgba(13,18,15,.26)`): Separates the right-side form from the dimmed page.
- **Mobile dossier** (`0 -20px 50px rgba(13,18,15,.26)`): Separates the bottom panel from the collection.

**The Overlay Rule.** Save pronounced shadows for panels that cover the workspace; use borders and tonal contrast for resting content.

## Shapes

Controls use small, nearly square corners (`rounded.control`). Table rows, the masthead, hero, summary, and dossier are straight edged. The rotated diamond crest is the identity's distinctive geometry; the small connection dot is functional status rather than a general rounding style.

## Components

### Buttons

- **Shape:** Compact rectangular controls (`rounded.control`) with a 42px minimum height and 16px horizontal padding; the masthead action is 44px high.
- **Primary:** Oxblood with light text for save and empty-state actions; darkens on hover.
- **Quiet:** Paper-context text with a fine line border; gains a deeper paper fill on hover.
- **Danger:** Red text and a muted red outline; receives a pale red hover fill. In the dark dossier, its colors shift lighter for contrast.
- **Focus:** Shared visible copper outline (3px, 3px offset). State changes use a short 160ms ease transition.

### Cards / Containers

- **Vehicle dossier:** A graphite panel with ivory text, a short copper rule, an outlined registration plate, two-column specifications, and edit/delete actions. Selecting a row updates it while the list remains visible on wide screens.
- **Empty state:** A deep-paper field under the inventory's strong top rule, with an invitation to add the first record. Search misses and server failures change the message and available action.

### Inputs / Fields

- **Search:** Warm off-white field (44px high), inset search icon, fine border, and oxblood focus border with a soft ring.
- **Editor fields:** Warm white fields (42px high) with compact labels; readonly ID gains a muted fill. On desktop the native modal dialog is a right-side panel spanning the viewport height without internal scrolling; shorter viewports use tighter spacing. Mobile allows the form to scroll. Error and success feedback stay inside the form.

### Navigation

The masthead is a compact graphite bar with the diamond crest, serif wordmark, inventory count, server status, and copper Add vehicle action. At narrow widths it wraps the count below the primary row. The footer keeps a simple API link in oxblood.

## Do's and Don'ts

### Do:

- **Do** keep vehicle names visually prominent and registrations and prices easy to compare.
- **Do** pair serif display moments with sans controls and data.
- **Do** use the paper, ink, oxblood, and copper roles consistently across personal and dealer workflows.
- **Do** retain visible focus and clear empty, error, and success states.

### Don't:

- **Don't** turn the working inventory into a loose grid of white cards.
- **Don't** apply overlay shadows to every resting surface.
- **Don't** use decorative automotive motifs in place of clear controls or data labels.
