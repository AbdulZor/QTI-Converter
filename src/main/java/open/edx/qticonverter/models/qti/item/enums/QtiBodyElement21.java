package open.edx.qticonverter.models.qti.item.enums;

public enum QtiBodyElement21 {
    address, h1, h2, h3, h4, h5, h6, p, pre,    // <- atomicBlock
    br, img,                                    // <- atomicInline
    caption,
    associableChoice, hotspotChoice, hottext, inlineChoice, simpleChoice,   // <- choice
    col, colgroup, div, dl, hr,

    associateInteraction, choiceInteraction, drawingInteraction, extendedTextInteraction,   // <-  blockInteraction
    gapMatchInteraction, graphicInteraction, hottextInteraction, matchInteraction,          // <-  blockInteraction
    mediaInteraction, orderInteraction, sliderInteraction, uploadInteraction,               // <-  blockInteraction

    endAttemptInteraction, inlineChoiceInteraction, textEntryInteraction,                   // <-  inlineInteraction

    positionObjectInteraction,

    itemBody, li,
    object, ol, printedValue, prompt, simpleBlock,

    a, abbr, acronym, b, big, cite, code, dfn, em, feedbackInline, i, kbd, q, samp,     // <- simpleInline
    small, span, string, sub, sup, tt, var,                                             // <- simpleInline

    table, tbody, templateElement, tfoot,
    thead, tr, ul, infoControl
}
