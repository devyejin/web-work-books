package org.zerock.b02.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.zerock.b02.domain.Reply;
import org.zerock.b02.dto.PageRequestDTO;
import org.zerock.b02.dto.PageResponseDTO;
import org.zerock.b02.dto.ReplyDTO;
import org.zerock.b02.repository.ReplyRepository;

import javax.persistence.Temporal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;


@Service
@Log4j2
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    protected final ModelMapper modelMapper;

    @Override
    public Long register(ReplyDTO replyDTO) {

        //DAO에는 VO로 넣어줘야 하므로 Mapper필요
        Reply reply = modelMapper.map(replyDTO, Reply.class);

        Reply savedReply = replyRepository.save(reply);

        return savedReply.getRno(); //저장된 리플번호 반환
    }

    @Override
    public ReplyDTO read(Long rno) {
        Optional<Reply> repylOptional = replyRepository.findById(rno);
        Reply reply = repylOptional.orElseThrow();

        return modelMapper.map(reply, ReplyDTO.class);
    }

    @Override
    public void modify(ReplyDTO replyDTO) {
        //수정해달라고 요청이 들어옴
        Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno()); //먼저 요청들어온 reply객체를 찾아오고
        Reply reply = replyOptional.orElseThrow();

        reply.changeText(replyDTO.getReplyText());

        replyRepository.save(reply);
    }

    @Override
    public void remove(Long rno) {
            replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("rno").ascending());

        //근데,,, ?
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        //response에 Page객체가 아닌 ReplyDTO로 반환해줘야 함
        List<ReplyDTO> dtoList = result.getContent().stream().map(reply ->
            modelMapper.map(reply, ReplyDTO.class)
        ).collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }



}
