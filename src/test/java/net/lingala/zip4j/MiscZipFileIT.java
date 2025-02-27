package net.lingala.zip4j;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.testutils.TestUtils;
import net.lingala.zip4j.testutils.ZipFileVerifier;
import net.lingala.zip4j.util.FileUtils;
import net.lingala.zip4j.util.InternalZipConstants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class MiscZipFileIT extends AbstractIT {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testMergeSplitZipFilesMergesSuccessfully() throws IOException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    List<File> filesToAdd = new ArrayList<>(FILES_TO_ADD);
    filesToAdd.add(TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    zipFile.createSplitZipFile(filesToAdd, new ZipParameters(), true, InternalZipConstants.MIN_SPLIT_LENGTH);

    File mergedZipFile = new File(temporaryFolder.getRoot().getPath() + InternalZipConstants.FILE_SEPARATOR
        + "merged_zip_file.zip");
    zipFile.mergeSplitFiles(mergedZipFile);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(mergedZipFile, outputFolder, 4);
  }

  @Test
  public void testMergeSplitZipFilesWithAesEncryptionMergesSuccessfully() throws IOException {
    ZipParameters zipParameters = createZipParameters(EncryptionMethod.AES, AesKeyStrength.KEY_STRENGTH_256);
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    List<File> filesToAdd = new ArrayList<>(FILES_TO_ADD);
    filesToAdd.add(TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    zipFile.createSplitZipFile(filesToAdd, zipParameters, true, InternalZipConstants.MIN_SPLIT_LENGTH);

    File mergedZipFile = new File(temporaryFolder.getRoot().getPath() + InternalZipConstants.FILE_SEPARATOR
        + "merged_zip_file.zip");
    zipFile.mergeSplitFiles(mergedZipFile);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(mergedZipFile, PASSWORD, outputFolder, 4);
  }

  @Test
  public void testGetFileHeadersReturnsEmptyListForNewZip() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    assertThat(zipFile.getFileHeaders()).isEmpty();
  }

  @Test
  public void testGetFileHeadersReturnsAllHeaders() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    List<FileHeader> fileHeaders = zipFile.getFileHeaders();

    assertThat(fileHeaders).isNotNull();
    assertThat(fileHeaders).hasSize(FILES_TO_ADD.size());
    List<String> fileNames = FILES_TO_ADD.stream().map(File::getName).collect(Collectors.toList());
    verifyFileHeadersContainsFiles(fileHeaders, fileNames);
  }

  @Test
  public void testGetFileHeadersReturnsAllHeadersAfterAddingAnotherFile() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    zipFile.addFile(TestUtils.getTestFileFromResources("бореиская.txt"));

    List<FileHeader> fileHeaders = zipFile.getFileHeaders();

    assertThat(fileHeaders).isNotNull();
    assertThat(fileHeaders).hasSize(FILES_TO_ADD.size() + 1);
    List<String> fileNames = FILES_TO_ADD.stream().map(File::getName).collect(Collectors.toList());
    fileNames.add("бореиская.txt");
    verifyFileHeadersContainsFiles(fileHeaders, fileNames);
  }

  @Test
  public void testGetFileHeaderReturnsNullForNewZip() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    assertThat(zipFile.getFileHeader("SOME_NAME")).isNull();
  }

  @Test
  public void testGetFileHeaderReturnsNullWhenFileDoesNotExist() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    assertThat(zipFile.getFileHeader("SOME_NAME")).isNull();
  }

  @Test
  public void testGetFileHeaderReturnsFileHeaderSuccessfully() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    FileHeader fileHeader = zipFile.getFileHeader("sample_text_large.txt");

    assertThat(fileHeader).isNotNull();
    assertThat(fileHeader.getFileName()).isEqualTo("sample_text_large.txt");
  }

  @Test
  public void testGetFileHeaderReturnsFileHeaderSuccessfullyAfterAddingNewFile() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    String fileToAdd = "file_PDF_1MB.pdf";
    zipFile.addFile(TestUtils.getTestFileFromResources(fileToAdd));

    FileHeader fileHeader = zipFile.getFileHeader(fileToAdd);

    assertThat(fileHeader).isNotNull();
    assertThat(fileHeader.getFileName()).isEqualTo(fileToAdd);
  }

  @Test
  public void testIsEncryptedReturnsFalseForNewZip() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    assertThat(zipFile.isEncrypted()).isFalse();
  }

  @Test
  public void testIsEncryptedReturnsFalseForNonEncryptedZip() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    assertThat(zipFile.isEncrypted()).isFalse();
  }

  @Test
  public void testIsEncryptedReturnsTrueForStandardZipEncryption() throws ZipException {
    ZipParameters zipParameters = createZipParameters(EncryptionMethod.ZIP_STANDARD, null);
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    zipFile.addFiles(FILES_TO_ADD, zipParameters);

    assertThat(zipFile.isEncrypted()).isTrue();
  }

  @Test
  public void testIsEncryptedReturnsTrueForAesEncryption() throws ZipException {
    ZipParameters zipParameters = createZipParameters(EncryptionMethod.AES, AesKeyStrength.KEY_STRENGTH_256);
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    zipFile.addFiles(FILES_TO_ADD, zipParameters);

    assertThat(zipFile.isEncrypted()).isTrue();
  }

  @Test
  public void testIsEncryptedReturnsTrueAfterAddingAnEncryptedFile() throws ZipException {
    ZipParameters zipParameters = createZipParameters(EncryptionMethod.AES, AesKeyStrength.KEY_STRENGTH_256);
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    zipFile.addFiles(FILES_TO_ADD);

    zipFile.addFile(TestUtils.getTestFileFromResources("sample_text_large.txt"), zipParameters);

    assertThat(zipFile.isEncrypted()).isTrue();
  }

  @Test
  public void testIsEncryptedReturnsFalseAfterRemovingAllEncryptedFiles() throws ZipException {
    ZipParameters zipParameters = createZipParameters(EncryptionMethod.AES, AesKeyStrength.KEY_STRENGTH_256);
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    zipFile.addFiles(FILES_TO_ADD);

    zipFile.addFile(TestUtils.getTestFileFromResources("sample_text_large.txt"), zipParameters);
    zipFile.removeFile("sample_text_large.txt");

    assertThat(zipFile.isEncrypted()).isFalse();
  }

  @Test
  public void testIsSplitArchiveReturnsFalseForNewlyCreatedZip() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    assertThat(zipFile.isSplitArchive()).isFalse();
  }

  @Test
  public void testIsSplitArchiveReturnsFalseForNonSplitZip() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    assertThat(zipFile.isSplitArchive()).isFalse();
  }

  @Test
  public void testIsSplitArchiveReturnsTrueForSplitZip() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    List<File> filesToAdd = new ArrayList<>(FILES_TO_ADD);
    filesToAdd.add(TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    zipFile.createSplitZipFile(filesToAdd, new ZipParameters(), true, InternalZipConstants.MIN_SPLIT_LENGTH);

    assertThat(zipFile.isSplitArchive()).isTrue();
  }

  @Test
  public void testIsSplitArchiveReturnsFalseWhenCreatedAsSplitZipButNotSplit() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.createSplitZipFile(FILES_TO_ADD, new ZipParameters(), true, InternalZipConstants.MIN_SPLIT_LENGTH);

    zipFile = new ZipFile(generatedZipFile);
    assertThat(zipFile.isSplitArchive()).isFalse();
  }

  @Test
  public void testIsSplitArchiveReturnsFalseForMergedZipFile() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    List<File> filesToAdd = new ArrayList<>(FILES_TO_ADD);
    filesToAdd.add(TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    zipFile.createSplitZipFile(filesToAdd, new ZipParameters(), true, InternalZipConstants.MIN_SPLIT_LENGTH);

    File mergedZipFile = new File(temporaryFolder.getRoot().getPath() + InternalZipConstants.FILE_SEPARATOR
        + "merged.zip");
    zipFile.mergeSplitFiles(mergedZipFile);

    zipFile = new ZipFile(mergedZipFile);
    assertThat(zipFile.isSplitArchive()).isFalse();
  }

  @Test
  public void testSetComment() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    zipFile.setComment("SOME_COMMENT");

    zipFile = new ZipFile(generatedZipFile);
    assertThat(zipFile.getComment()).isEqualTo("SOME_COMMENT");
  }

  @Test
  public void testSetCommentForMergedZipRetainsComment() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    List<File> filesToAdd = new ArrayList<>(FILES_TO_ADD);
    filesToAdd.add(TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    zipFile.createSplitZipFile(filesToAdd, new ZipParameters(), true, InternalZipConstants.MIN_SPLIT_LENGTH);

    String comment = "SOME_COMMENT";
    zipFile.setComment(comment);
    assertThat(zipFile.getComment()).isEqualTo(comment);

    File mergedZipFile = new File(temporaryFolder.getRoot().getPath() + InternalZipConstants.FILE_SEPARATOR
        + "merged.zip");
    zipFile.mergeSplitFiles(mergedZipFile);

    zipFile = new ZipFile(mergedZipFile);
    assertThat(zipFile.getComment()).isEqualTo(comment);
  }

  @Test
  public void testSetCommentWithEmptyStringRemovesComment() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    String comment = "SOME_COMMENT";
    zipFile.setComment(comment);
    assertThat(zipFile.getComment()).isEqualTo(comment);

    zipFile.setComment("");
    assertThat(zipFile.getComment()).isEqualTo("");

    //Make sure comment is empty and not null also when a new instance is now created
    zipFile = new ZipFile(generatedZipFile);
    assertThat(zipFile.getComment()).isEqualTo("");
  }

  @Test
  public void testGetInputStreamWithoutEncryptionReturnsSuccessfully() throws IOException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    try (InputStream inputStream = zipFile.getInputStream(zipFile.getFileHeader("sample_text_large.txt"))) {
      assertThat(inputStream).isNotNull();
      verifyInputStream(inputStream, TestUtils.getTestFileFromResources("sample_text_large.txt"));
    }

  }

  @Test
  public void testGetInputStreamWithAesEncryptionReturnsSuccessfully() throws IOException {
    ZipParameters zipParameters = createZipParameters(EncryptionMethod.AES, AesKeyStrength.KEY_STRENGTH_256);
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    zipFile.addFiles(FILES_TO_ADD, zipParameters);

    try (InputStream inputStream = zipFile.getInputStream(zipFile.getFileHeader("sample_text_large.txt"))) {
      assertThat(inputStream).isNotNull();
      verifyInputStream(inputStream, TestUtils.getTestFileFromResources("sample_text_large.txt"));
    }
  }

  @Test
  public void testGetInputStreamWithAesEncryptionAndSplitFileReturnsSuccessfully() throws IOException {
    ZipParameters zipParameters = createZipParameters(EncryptionMethod.AES, AesKeyStrength.KEY_STRENGTH_256);
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    List<File> filesToAdd = new ArrayList<>(FILES_TO_ADD);
    filesToAdd.add(TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    zipFile.createSplitZipFile(filesToAdd, zipParameters, true, InternalZipConstants.MIN_SPLIT_LENGTH);

    try (InputStream inputStream = zipFile.getInputStream(zipFile.getFileHeader("file_PDF_1MB.pdf"))) {
      assertThat(inputStream).isNotNull();
      verifyInputStream(inputStream, TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    }

    //Check also with a new instance
    zipFile = new ZipFile(generatedZipFile, PASSWORD);
    try (InputStream inputStream = zipFile.getInputStream(zipFile.getFileHeader("file_PDF_1MB.pdf"))) {
      verifyInputStream(inputStream, TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    }
  }

  @Test
  public void testIsValidZipFileReturnsFalseForNonZipFile() {
    assertThat(new ZipFile(TestUtils.getTestFileFromResources("sample_text_large.txt")).isValidZipFile()).isFalse();
  }

  @Test
  public void testIsValidZipFileReturnsFalseForNonExistingZip() {
    assertThat(new ZipFile("DoesNoExist").isValidZipFile()).isFalse();
  }

  @Test
  public void testIsValidZipFileReturnsTrueForAValidZip() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    assertThat(zipFile.isValidZipFile()).isTrue();
  }

  @Test
  public void testGetSplitZipFilesReturnsJustZipFileForNonSplit() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    zipFile.addFiles(FILES_TO_ADD);

    List<File> splitZipFiles = zipFile.getSplitZipFiles();

    assertThat(splitZipFiles).hasSize(1);
    assertThat(splitZipFiles.get(0)).hasName(generatedZipFile.getName());
  }

  @Test
  public void testGetSplitZipFilesReturnsAllSplitZipFiles() throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile);
    List<File> filesToAdd = new ArrayList<>(FILES_TO_ADD);
    filesToAdd.add(TestUtils.getTestFileFromResources("file_PDF_1MB.pdf"));
    zipFile.createSplitZipFile(filesToAdd, new ZipParameters(), true, InternalZipConstants.MIN_SPLIT_LENGTH);

    List<File> splitZipFiles = zipFile.getSplitZipFiles();

    assertThat(splitZipFiles).hasSize(15);
    verifySplitZipFileNames(splitZipFiles, 15, FileUtils.getZipFileNameWithoutExtension(generatedZipFile.getName()));
  }

  @Test
  public void testRenameZipFileAfterExtractionWithInputStreamSucceeds() throws IOException {
    new ZipFile(generatedZipFile).addFiles(FILES_TO_ADD);

    ZipFile zipFile = new ZipFile(generatedZipFile);
    FileHeader fileHeader = zipFile.getFileHeader("sample_text1.txt");

    assertThat(fileHeader).isNotNull();

    try(InputStream inputStream = zipFile.getInputStream(fileHeader)) {
      inputStream.read(new byte[100]);
    }

    File newFile = temporaryFolder.newFile("NEW_FILE_NAME.ZIP");
    String oldFile = generatedZipFile.getPath();

    if(TestUtils.isWindows())
    {
      newFile.delete();
    }

    assertThat(generatedZipFile.renameTo(newFile)).isTrue();
    assertThat(new File(oldFile)).doesNotExist();
  }

  @Test
  public void testZipSlipFix() throws Exception {
    ZipParameters zipParameters = new ZipParameters();
    zipParameters.setFileNameInZip("../../bad.txt");

    ZipFile zip = new ZipFile(generatedZipFile);
    zip.addFile(TestUtils.getTestFileFromResources("sample_text1.txt"), zipParameters);

    try {
      zip.extractAll(outputFolder.getAbsolutePath());
      fail("zip4j is vulnerable for slip zip");
    } catch (ZipException e) {
      assertThat(e).hasMessageStartingWith("illegal file name that breaks out of the target directory: ");
    }
  }

  private void verifyInputStream(InputStream inputStream, File fileToCompareAgainst) throws IOException {
    File outputFile = temporaryFolder.newFile();
    try (OutputStream outputStream = new FileOutputStream(outputFile)) {
      byte[] b = new byte[InternalZipConstants.BUFF_SIZE];
      int readLen = -1;

      while ((readLen = inputStream.read(b)) != -1) {
        outputStream.write(b, 0, readLen);
      }
    }

    ZipFileVerifier.verifyFileContent(fileToCompareAgainst, outputFile);
  }

  private void verifySplitZipFileNames(List<File> files, int expectedNumberOfZipFiles,
                                       String fileNameWithoutExtension) {
    assertThat(files).hasSize(expectedNumberOfZipFiles);

    for (int i = 0; i < expectedNumberOfZipFiles; i++) {
      File file = files.get(i);
      String fileExtensionPrefix = ".z0";

      if (i >= 9) {
        fileExtensionPrefix = ".z";
      }

      String expectedFileName = fileNameWithoutExtension + fileExtensionPrefix + (i + 1);
      if (i == expectedNumberOfZipFiles - 1) {
        expectedFileName = fileNameWithoutExtension + ".zip";
      }

      assertThat(file).hasName(expectedFileName);
    }
  }

}
