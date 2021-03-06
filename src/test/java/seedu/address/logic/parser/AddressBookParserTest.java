package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAliases.getTypicalAlias;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddAliasCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteAliasCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListAliasCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.DisplayFilterPredicate;
import seedu.address.model.UniqueAliasMap;
import seedu.address.model.alias.CommandAlias;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.CommandAliasBuilder;
import seedu.address.testutil.CommandAliasUtil;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();
    private final UniqueAliasMap emptyAliases = new UniqueAliasMap();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person), emptyAliases);
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD, emptyAliases) instanceof ClearCommand);
        assertTrue(parser.parseCommand(
                ClearCommand.COMMAND_WORD + " 3", emptyAliases) instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased(), emptyAliases);
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor), emptyAliases);
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD, emptyAliases) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3", emptyAliases) instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " "
                        + keywords.stream().collect(Collectors.joining(" ")), emptyAliases);
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_filter() throws Exception {
        List<String> keywords = Arrays.asList(PREFIX_ADDRESS.toString(), PREFIX_PHONE.toString(),
                PREFIX_EMAIL.toString());
        ArgumentMultimap argumentMultimap = new ArgumentMultimap();
        argumentMultimap.put(PREFIX_ADDRESS, "");
        argumentMultimap.put(PREFIX_PHONE, "");
        argumentMultimap.put(PREFIX_EMAIL, "");
        DisplayFilterPredicate displayFilterPredicate = new DisplayFilterPredicate(argumentMultimap);
        FilterCommand command = (FilterCommand) parser.parseCommand(FilterCommand.COMMAND_WORD + " "
                + keywords.stream().collect(Collectors.joining(" ")), emptyAliases);
        assertEquals(new FilterCommand(displayFilterPredicate), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD, emptyAliases) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3", emptyAliases) instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD, emptyAliases) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3", emptyAliases) instanceof ListCommand);
    }

    @Test
    public void parseCommand_aliasAdd() throws Exception {
        CommandAlias commandAlias = new CommandAliasBuilder().build();
        AddAliasCommand command = (AddAliasCommand) parser.parseCommand(
                CommandAliasUtil.getAddAliasCommand(commandAlias), emptyAliases);
        assertEquals(new AddAliasCommand(commandAlias), command);
    }

    @Test
    public void parseCommand_aliasDelete() throws Exception {
        DeleteAliasCommand command = (DeleteAliasCommand) parser.parseCommand(DeleteAliasCommand.COMMAND_WORD
                + " " + DeleteAliasCommand.DELETE_SUB_COMMAND_WORD + " " + getTypicalAlias(), emptyAliases);
        assertEquals(new DeleteAliasCommand(getTypicalAlias()), command);
    }

    @Test
    public void parseCommand_aliasList() throws Exception {
        assertTrue(parser.parseCommand(ListAliasCommand.COMMAND_WORD + " "
                + ListAliasCommand.LIST_SUB_COMMAND_WORD, emptyAliases) instanceof ListAliasCommand);
        assertTrue(parser.parseCommand(ListAliasCommand.COMMAND_WORD + " "
                + ListAliasCommand.LIST_SUB_COMMAND_WORD + " 3", emptyAliases) instanceof ListAliasCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand("", emptyAliases));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(
                "unknownCommand", emptyAliases));
    }

}
