package com.ibm.sttcustomization.examples;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.validator.AbstractValidator;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.Objects;
import java.util.Optional;

/**
 * Contact editor form.
 *
 * @author Vaadin Ltd
 *
 */
//@StyleSheet("frontend://src/style.css")
@Route("registrationexample")
@Theme(Lumo.class)
public class RegistrantionExample extends Composite<VerticalLayout> {
    public class PasswordValidator extends StringLengthValidator {

        /**
         * Creates a new instance.
         */
        public PasswordValidator() {
            super("", 6, Integer.MAX_VALUE);
        }

        @Override
        public ValidationResult apply(String value, ValueContext context) {
            ValidationResult result = super.apply(value, context);
            if (result.isError()) {
                return ValidationResult
                        .error("Password should contain at least 6 characters");
            } else if (!hasDigit(value) || !hasLetter(value)) {
                return ValidationResult
                        .error("Password must contain a letter and a number");
            }
            return result;
        }

        private boolean hasDigit(String pwd) {
            return pwd.chars().anyMatch(Character::isDigit);
        }

        private boolean hasLetter(String pwd) {
            return pwd.chars().anyMatch(Character::isLetter);
        }
    }

    public class RegistrationPasswordField extends PasswordField
            implements AbstractTextField<PasswordField> {

        private Object data;

        @Override
        public Object getData() {
            return data;
        }

        @Override
        public void setData(Object object) {
            data = object;
        }
    }


    class EmailOrPhoneValidator extends AbstractValidator<String> {

        private final EmailValidator emailValidator;

        EmailOrPhoneValidator() {
            super("");
            emailValidator = new EmailValidator(
                    "The string '{0}' is not a valid email address");
        }

        @Override
        public ValidationResult apply(String value, ValueContext context) {
            String val = value;
            // remove all spaces
            val = val.replace(" ", "");
            // if string starts from +0-9 ignoring spaces
            if (!startsWithCountryCode(val)) {
                return emailValidator.apply(value, context);
            }
            String digits = val.substring(1);
            // if string contains only + and digits (ignoring spaces)
            if (!hasOnlyDigits(digits)) {
                return ValidationResult.error(String.format(
                        "The string '%s' is not a valid phone number. "
                                + "Phone numbers should start with a plus sign followed by digits.",
                        value));
            }
            // now there should be at least 10 digits
            if (digits.length() >= 10) {
                return ValidationResult.ok();
            }
            return ValidationResult.error(String.format(
                    "The string '%s' is not a valid phone number. "
                            + "Phone should start with a plus sign and contain at least 10 digits",
                    value));
        }

        private boolean startsWithCountryCode(String phone) {
            return phone.length() >= 2 && phone.charAt(0) == '+'
                    && Character.isDigit(phone.charAt(1));
        }

        private boolean hasOnlyDigits(String phone) {
            return phone.chars().allMatch(Character::isDigit);
        }

    }


    public interface AbstractTextField<T> extends HasSize {

        /**
         * A hint to the user of what can be entered in the control.
         *
         * @param placeholder
         *            the String value to set
         * @return this instance, for method chaining
         */
        void setPlaceholder(String placeholder);

        /**
         * Sets an arbitrary data into the instance.
         *
         * @see #getData()
         *
         * @param object
         *            a data to set
         */
        void setData(Object object);

        /**
         * Gets the data set via {@link #setData(Object)}.
         *
         * @see #setData(Object)
         * @return the stored data
         */
        Object getData();

        /**
         * Gets the id of the component.
         *
         * @return the element id.
         */
        Optional<String> getId();
    }


    public class RegistrationTextField extends TextField
            implements AbstractTextField<TextField> {

        private Object data;

        @Override
        public Object getData() {
            return data;
        }

        @Override
        public void setData(Object object) {
            data = object;
        }
    }


    public class Person {

        private String fullName;

        private String emailOrPhone;

        private String password;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmailOrPhone() {
            return emailOrPhone;
        }

        public void setEmailOrPhone(String emailOrPhone) {
            this.emailOrPhone = emailOrPhone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }


    private static final String ERROR = "error";

    private static final String OK = "ok";

    private static final String WIDTH = "350px";

    private final Binder<Person> binder = new Binder<>();

    private Binder.Binding<Person, String> passwordBinding;
    private Binder.Binding<Person, String> confirmPasswordBinding;

    private boolean showConfirmPasswordStatus;

    private static final String VALID = "valid";

    /**
     * Creates a new instance of the form.
     */
    public RegistrantionExample() {



        RegistrationTextField fullNameField = new RegistrationTextField();
        fullNameField.setId("full-name");
        fullNameField.setValueChangeMode(ValueChangeMode.EAGER);
        addToLayout(fullNameField, "Full name");

        binder.forField(fullNameField).asRequired("Full name may not be empty")
                .withValidationStatusHandler(
                        status -> commonStatusChangeHandler(status,
                                fullNameField))
                .bind(Person::getFullName, Person::setFullName);

        RegistrationTextField phoneOrEmailField = new RegistrationTextField();
        phoneOrEmailField.setId("phone-or-email");
        phoneOrEmailField.setValueChangeMode(ValueChangeMode.EAGER);
        addToLayout(phoneOrEmailField, "Phone or Email");
        binder.forField(phoneOrEmailField)
                .withValidator(new EmailOrPhoneValidator())
                .withValidationStatusHandler(
                        status -> commonStatusChangeHandler(status,
                                phoneOrEmailField))
                .bind(Person::getEmailOrPhone, Person::setEmailOrPhone);

        RegistrationPasswordField passwordField = new RegistrationPasswordField();
        passwordField.setId("pwd");
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        addToLayout(passwordField, "Password");
        passwordBinding = binder.forField(passwordField)
                .withValidator(new PasswordValidator())
                .withValidationStatusHandler(
                        status -> commonStatusChangeHandler(status,
                                passwordField))
                .bind(Person::getPassword, Person::setPassword);
        passwordField.addValueChangeListener(
                event -> confirmPasswordBinding.validate());

        RegistrationPasswordField confirmPasswordField = new RegistrationPasswordField();
        confirmPasswordField.setId("confirm-pwd");
        confirmPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
        addToLayout(confirmPasswordField, "Password again");

        confirmPasswordBinding = binder.forField(confirmPasswordField)
                .withValidator(Validator.from(this::validateConfirmPasswd,
                        "Password doesn't match"))
                .withValidationStatusHandler(
                        status -> confirmPasswordStatusChangeHandler(status,
                                confirmPasswordField))
                .bind(Person::getPassword, (person, pwd) -> {
                });

        getContent().add(createButton());

        fullNameField.focus();

        binder.setBean(new Person());
    }

    private void addToLayout(AbstractTextField<?> textField,
                             String placeHolderText) {
        textField.setPlaceholder(placeHolderText);
        Label statusMessage = new Label();
        assert textField.getId().isPresent();
        statusMessage.setId(textField.getId().get() + "-status");
        setVisible(statusMessage, false);
        statusMessage.getClassNames().add("validation-message");
        textField.setData(statusMessage);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add((Component) textField);
        textField.setWidth(WIDTH);
        horizontalLayout.add(statusMessage);
        getContent().add(horizontalLayout);
    }

    private Button createButton() {
        Button button = new Button("Sign Up", event -> save());
        button.getClassNames().add("primary");
        button.setWidth(WIDTH);
        button.setId("sign-up");
        return button;
    }

    private void commonStatusChangeHandler(BindingValidationStatus<?> event,
                                           AbstractTextField<?> field) {
        Label statusLabel = (Label) field.getData();
        setVisible(statusLabel, !event.getStatus()
                .equals(BindingValidationStatus.Status.UNRESOLVED));
        switch (event.getStatus()) {
            case OK:
                statusLabel.setText("");
                statusLabel.setClassName(OK);
                statusLabel.removeClassName(ERROR);
                ((HasStyle) statusLabel.getParent().get()).setClassName(VALID);
                break;
            case ERROR:
                statusLabel.removeClassName(OK);
                statusLabel.setClassName(ERROR);
                statusLabel.setText(event.getMessage().orElse("Unknown error"));
                ((HasStyle) statusLabel.getParent().get()).removeClassName(VALID);
            default:
                break;
        }
    }

    private void confirmPasswordStatusChangeHandler(
            BindingValidationStatus<?> event, AbstractTextField<?> field) {
        commonStatusChangeHandler(event, field);
        Label statusLabel = (Label) field.getData();
        setVisible(statusLabel, showConfirmPasswordStatus);
    }

    private boolean validateConfirmPasswd(String confirmPasswordValue) {
        showConfirmPasswordStatus = false;
        if (confirmPasswordValue.isEmpty()) {
            return true;

        }
        BindingValidationStatus<String> status = passwordBinding.validate();
        if (status.isError()) {
            return true;
        }
        showConfirmPasswordStatus = true;
        HasValue<?, ?> pwdField = passwordBinding.getField();
        return Objects.equals(pwdField.getValue(), confirmPasswordValue);
    }

    private void save() {
        Person person = new Person();
        if (binder.writeBeanIfValid(person)) {
            showNotification("Registration data saved successfully",
                    String.format("Full name '%s', email or phone '%s'",
                            person.getFullName(), person.getEmailOrPhone()),
                    false);
        } else {
            showNotification("Error",
                    "Registration could not be saved, please check all fields",
                    true);
        }
    }

    private void showNotification(String title, String message, boolean error) {
        Dialog dialog = createDialog(title, message, error);

        getUI().get().add(dialog);
        dialog.open();
    }

    private Dialog createDialog(String title, String text,
                                boolean error) {
        Dialog dialog = new Dialog();
        dialog.setId("notification");
        dialog.add(new H2(title));
        HtmlComponent paragraph = new HtmlComponent(Tag.P);
        paragraph.getElement().setText(text);
        if (error) {
            paragraph.setClassName(ERROR);
        }
        dialog.add(paragraph);
        return dialog;
    }

    private static void setVisible(Label label, boolean visible) {
        if (visible) {
            label.getElement().getStyle().remove("display");
        } else {
            label.getElement().getStyle().set("display", "none");
        }
    }
}
