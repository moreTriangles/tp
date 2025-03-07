package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.scene.image.Image;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.GitHubUtil;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Github;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
public class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String telegram;
    private final String github;
    private final String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tagged = new ArrayList<>();
    private boolean isFavorite;
    private final HashMap<String, Double> gitStats;
    private final Image image;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(
            @JsonProperty("name") String name,
            @JsonProperty("telegram") String telegram,
            @JsonProperty("github") String github,
            @JsonProperty("phone") String phone,
            @JsonProperty("email") String email,
            @JsonProperty("address") String address,
            @JsonProperty("tagged") List<JsonAdaptedTag> tagged,
            @JsonProperty("isFavorite") boolean isFavorite,
            @JsonProperty("gitStats") HashMap<String, Double> gitStats,
            @JsonProperty("image") Image image) {
        this.name = name;
        this.telegram = telegram;
        this.github = github;
        this.phone = phone;
        this.email = email;
        this.address = address;
        if (tagged != null) {
            this.tagged.addAll(tagged);
        }
        this.isFavorite = isFavorite;
        this.gitStats = gitStats;
        this.image = image;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        telegram = source.getTelegram().value;
        github = source.getGithub().value;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        tagged.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        isFavorite = source.isFavorite();
        image = source.getProfilePicture();
        gitStats = source.getGitStats();
    }

    /**
     * Checks whether given Image is the default profile image
     *
     * @param image image to check
     * @return true, if given image is same as default
     */
    public boolean isDefaultImage(Image image) {

        if (image == null
                || image.getHeight() != GitHubUtil.DEFAULT_USER_PROFILE_PICTURE.getHeight()
                || image.getWidth() != GitHubUtil.DEFAULT_USER_PROFILE_PICTURE.getWidth()) {
            return false;
        }

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int firstArgb = image.getPixelReader().getArgb(x, y);
                int secondArgb = GitHubUtil.DEFAULT_USER_PROFILE_PICTURE.getPixelReader().getArgb(x, y);
                if (firstArgb != secondArgb) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tagged) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (telegram == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Telegram.class.getSimpleName()));
        }
        if (!Telegram.isValidTelegram(telegram)) {
            throw new IllegalValueException(Telegram.MESSAGE_CONSTRAINTS);
        }
        final Telegram modelTelegram = new Telegram(telegram);

        if (github == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Github.class.getSimpleName()));
        }
        if (!Github.isValidGithub(github)) {
            throw new IllegalValueException(Github.MESSAGE_CONSTRAINTS);
        }
        final Github modelGithub = new Github(github);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }

        if (!phone.isBlank() && !Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!email.isBlank() && !Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!address.isBlank() && !Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final boolean modelIsFavorite = isFavorite;

        final Set<Tag> modelTags = new HashSet<>(personTags);

        if (image == null || isDefaultImage(image)) {
            return new Person(modelName, modelTelegram, modelGithub, modelPhone,
                    modelEmail, modelAddress, modelTags, modelIsFavorite);
        }

        if (gitStats == null || gitStats.isEmpty()) {
            return new Person(modelName, modelTelegram, modelGithub, modelPhone,
                    modelEmail, modelAddress, modelTags, modelIsFavorite, image);
        } else {
            return new Person(modelName, modelTelegram, modelGithub, modelPhone,
                    modelEmail, modelAddress, modelTags, modelIsFavorite, image, gitStats);
        }
    }

}
